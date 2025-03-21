CREATE OR REPLACE FUNCTION public.insert_employee(
    IN first_name text,
    IN last_name text,
    IN position text
) RETURNS text AS $$
BEGIN
    INSERT INTO employee (first_name, last_name, "position")
    VALUES (first_name, last_name, position);
    RETURN 'SUCCESS';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.update_employee(
    IN emp_id integer,
    IN first_name text,
    IN last_name text,
    IN "position" text
) RETURNS void AS $$
BEGIN
    -- Check if employee exists
    IF NOT EXISTS (SELECT 1 FROM employee WHERE employee_id = emp_id) THEN
        RAISE EXCEPTION 'Employee with ID % does not exist', emp_id;
    END IF;

    -- Proceed with the update if employee exists
    UPDATE employee
    SET first_name = first_name,
        last_name = last_name,
        "position" = "position"
    WHERE employee_id = emp_id;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION public.get_last_employee_id()
    RETURNS integer
    LANGUAGE plpgsql
AS $$
DECLARE
    last_id integer;
BEGIN
    -- Get the maximum employee_id or return 0 if no records exist
    SELECT COALESCE(MAX(employee_id), 0) INTO last_id
    FROM employee;

    RETURN last_id;
END;
$$;

-- INVENTORY SECTION
CREATE OR REPLACE FUNCTION public.insert_inventory(
    IN sku text,
    IN name text,
    IN quantity integer
) RETURNS void AS $$
DECLARE
    inventory_exists boolean;
BEGIN
    -- Check if SKU already exists in the inventory (case-insensitive check)
    SELECT EXISTS (SELECT 1 FROM inventory WHERE UPPER(inventory.sku) = UPPER(sku)) INTO inventory_exists;

    -- If SKU exists, raise an exception
    IF inventory_exists THEN
        RAISE EXCEPTION 'An inventory item with the same SKU already exists.'
        USING ERRCODE = '23505';
    END IF;

    -- Insert the new inventory item
    INSERT INTO inventory (sku, name, quantity)
    VALUES (UPPER(sku), name, quantity);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.update_inventory(
    IN p_sku text,
    IN p_name text,
    IN p_quantity integer
) RETURNS void AS $$
DECLARE
    inventory_exists boolean;
BEGIN
    -- Check if the inventory item exists (case-insensitive check)
    SELECT EXISTS (SELECT 1 FROM inventory WHERE UPPER(inventory.sku) = UPPER(p_sku)) INTO inventory_exists;

    -- If inventory item does not exist, raise an exception
    IF NOT inventory_exists THEN
        RAISE EXCEPTION 'Inventory with SKU % does not exist.', UPPER(p_sku)
        USING HINT = 'Please verify the SKU of the inventory item.';
    END IF;

    -- Update the inventory item details
    UPDATE inventory
    SET name = p_name, quantity = p_quantity
    WHERE UPPER(inventory.sku) = UPPER(p_sku);
END;
$$ LANGUAGE plpgsql;

-- POLICIES SECTION

CREATE OR REPLACE FUNCTION public.insert_policy(
    IN p_employee_id integer,
    IN p_sku text,
    IN p_quantity integer,
    IN p_date text
) RETURNS void AS $$
DECLARE
    exists_employee boolean;
    exists_inventory boolean;
    current_inventory_quantity integer;
BEGIN
    -- Check if the employee exists
    SELECT EXISTS (SELECT 1 FROM employee WHERE id_employee = p_employee_id) INTO exists_employee;
    IF NOT exists_employee THEN
        RAISE EXCEPTION 'Employee with ID % does not exist.', p_employee_id
        USING HINT = 'Please verify the employee ID.';
    END IF;

    -- Check if the inventory item exists
    SELECT EXISTS (SELECT 1 FROM inventory WHERE sku = UPPER(p_sku)) INTO exists_inventory;
    IF NOT exists_inventory THEN
        RAISE EXCEPTION 'Inventory item with SKU % does not exist.', UPPER(p_sku)
        USING HINT = 'Please verify the SKU of the inventory item.';
    END IF;

    -- Check if there is enough quantity in inventory
    SELECT quantity INTO current_inventory_quantity FROM inventory WHERE sku = UPPER(p_sku);
    IF p_quantity > current_inventory_quantity THEN
        RAISE EXCEPTION 'Insufficient inventory for SKU %.', UPPER(p_sku)
        USING HINT = 'Please verify the quantity of the inventory item.';
    END IF;

    -- Insert a new policy record
    INSERT INTO policy (employee_generated, sku, quantity, date)
    VALUES (p_employee_id, UPPER(p_sku), p_quantity, p_date);

    -- Update the inventory with the reduced quantity
    UPDATE inventory
    SET quantity = quantity - p_quantity
    WHERE sku = UPPER(p_sku);

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.update_policy(
    IN p_policy_id integer,
    IN p_employee_id integer,
    IN p_sku text,
    IN p_quantity integer,
    IN p_date text
) RETURNS void AS $$
DECLARE
    employee_exists boolean;
    inventory_exists boolean;
    current_inventory_quantity integer;
    policy_inventory_quantity integer;
    current_sku_in_policy text;
BEGIN
    -- Check if the employee exists
    SELECT EXISTS (SELECT 1 FROM employee WHERE id_employee = p_employee_id) INTO employee_exists;
    IF NOT employee_exists THEN
        RAISE EXCEPTION 'Employee with ID % does not exist.', p_employee_id
        USING HINT = 'Please verify the employee ID.';
    END IF;

    -- Check if the inventory item exists
    SELECT EXISTS (SELECT 1 FROM inventory WHERE sku = UPPER(p_sku)) INTO inventory_exists;
    IF NOT inventory_exists THEN
        RAISE EXCEPTION 'Inventory item with SKU % does not exist.', UPPER(p_sku)
        USING HINT = 'Please verify the SKU of the inventory item.';
    END IF;

    -- Check if the inventory has enough quantity
    SELECT quantity INTO current_inventory_quantity FROM inventory WHERE sku = UPPER(p_sku);
    IF p_quantity > current_inventory_quantity THEN
        RAISE EXCEPTION 'Insufficient inventory for SKU %.', UPPER(p_sku)
        USING HINT = 'Please verify the quantity of the inventory item.';
    END IF;

    -- If the policy is updated with a different SKU, return the previous quantity to the old inventory
    SELECT sku INTO current_sku_in_policy FROM policy WHERE id_policy = p_policy_id;
    IF current_sku_in_policy != UPPER(p_sku) THEN
        SELECT quantity INTO policy_inventory_quantity FROM policy WHERE id_policy = p_policy_id;
        -- Return the previous quantity to the old inventory
        UPDATE inventory
        SET quantity = quantity + policy_inventory_quantity
        WHERE sku = current_sku_in_policy;
    END IF;

    -- Update the policy with the new employee, SKU, quantity, and date
    UPDATE policy
    SET employee_generated = p_employee_id,
        sku = UPPER(p_sku),
        quantity = p_quantity,
        date = p_date
    WHERE id_policy = p_policy_id;

    -- Update the inventory with the new quantity (reduce the inventory)
    UPDATE inventory
    SET quantity = quantity - p_quantity
    WHERE sku = UPPER(p_sku);

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.delete_policy(IN p_policy_id integer) RETURNS void AS $$
DECLARE
    policy_exists boolean;
    policy_quantity integer;
    policy_sku text;
BEGIN
    -- Check if the policy exists
    SELECT EXISTS (SELECT 1 FROM policy WHERE id_policy = p_policy_id) INTO policy_exists;
    IF NOT policy_exists THEN
        RAISE EXCEPTION 'Policy with ID % does not exist.', p_policy_id
        USING HINT = 'Please verify the policy ID.';
    END IF;

    -- Get the quantity and SKU from the policy to update inventory
    SELECT quantity, sku INTO policy_quantity, policy_sku FROM policy WHERE id_policy = p_policy_id;

    -- Update inventory to return the policy quantity
    UPDATE inventory
    SET quantity = quantity + policy_quantity
    WHERE sku = policy_sku;

    -- Delete the policy from the policy table
    DELETE FROM policy WHERE id_policy = p_policy_id;

END;
$$ LANGUAGE plpgsql;

-- This function already exists as a function, so no changes needed here
CREATE OR REPLACE FUNCTION public.get_last_policy_id() RETURNS integer
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Return the maximum policy id or 0 if no records exist
    RETURN COALESCE((SELECT MAX(id_policy) FROM policy), 0);
END;
$$;

CREATE OR REPLACE FUNCTION public.update_policy_fields(
    IN p_policy_id integer,
    IN p_employee_id integer,
    IN p_sku text,
    IN p_quantity integer
) RETURNS void AS $$
DECLARE
    inventory_exists boolean;
    employee_exists boolean;
    employee_gender_id integer;
    policy_sku text;
    inventory_quantity integer;
    policy_quantity integer;
BEGIN
    -- Check if the inventory exists
    SELECT EXISTS (SELECT sku FROM inventory WHERE sku = UPPER(p_sku)) INTO inventory_exists;
    IF NOT inventory_exists THEN
        RAISE EXCEPTION 'Inventory with SKU % does not exist.', UPPER(p_sku)
        USING HINT = 'Please verify the SKU of the inventory.';
    END IF;

    -- Check if the employee exists
    SELECT EXISTS (SELECT id_employee FROM employee WHERE id_employee = p_employee_id) INTO employee_exists;
    IF NOT employee_exists THEN
        RAISE EXCEPTION 'Employee with ID % does not exist.', p_employee_id
        USING HINT = 'Please verify the employee ID.';
    END IF;

    -- Check if the selected inventory is the same as the one in the policy, or if a different inventory is selected
    SELECT sku INTO policy_sku FROM policy WHERE id_policy = p_policy_id;
    IF policy_sku != UPPER(p_sku) THEN
        -- Check if the selected inventory has enough quantity
        SELECT quantity INTO inventory_quantity FROM inventory WHERE sku = UPPER(p_sku);
        IF p_quantity > inventory_quantity OR inventory_quantity = 0 THEN
            RAISE EXCEPTION 'Insufficient inventory for SKU %.', UPPER(p_sku)
            USING HINT = 'Please verify the quantity of the policy.';
        END IF;

        -- Return the quantity of the policy to the corresponding inventory
        SELECT quantity INTO policy_quantity FROM policy WHERE id_policy = p_policy_id;
        UPDATE inventory SET quantity = quantity + policy_quantity WHERE sku = policy_sku;

        -- Update the quantity of the selected inventory
        UPDATE inventory SET quantity = quantity - p_quantity WHERE sku = UPPER(p_sku);

        -- Update the policy with the new inventory
        UPDATE policy SET sku = UPPER(p_sku) WHERE id_policy = p_policy_id;
    ELSE
        -- Check if the new quantity is greater or smaller than the current policy quantity
        SELECT quantity INTO policy_quantity FROM policy WHERE id_policy = p_policy_id;
        IF p_quantity < policy_quantity THEN
            -- Return the difference of quantity to the inventory
            UPDATE inventory SET quantity = quantity + (policy_quantity - p_quantity) WHERE sku = policy_sku;
        ELSE
            -- Check if the inventory has enough quantity or is empty
            SELECT quantity INTO inventory_quantity FROM inventory WHERE sku = policy_sku;
            IF p_quantity > inventory_quantity OR inventory_quantity = 0 THEN
                RAISE EXCEPTION 'Insufficient inventory for SKU %.', policy_sku
                USING HINT = 'Please verify the quantity of the policy.';
            END IF;

            -- Subtract the difference between the new quantity and the current policy quantity from the inventory
            UPDATE inventory SET quantity = quantity - (p_quantity - policy_quantity) WHERE sku = policy_sku;
        END IF;
    END IF;

    -- Update the policy quantity and the employee associated with the policy
    UPDATE policy SET quantity = p_quantity, employee_generated = p_employee_id WHERE id_policy = p_policy_id;
END;
$$ LANGUAGE plpgsql;
