'use client';

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { PlusCircle, Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { 
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { 
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Policy, Employee, Inventory } from "@/types";

interface PoliciesTableProps {
  policies: Policy[];
  employees: Employee[]; 
  inventory: Inventory[];
  onAddPolicy: (policy: Partial<Policy>) => void;
  onEditPolicy?: (policyId: number, policy: Partial<Policy>) => void;
  onDeletePolicy?: (policyId: number) => void;
}

export function PoliciesTable({
  policies,
  employees,
  inventory,
  onAddPolicy,
  onEditPolicy,
  onDeletePolicy
}: PoliciesTableProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [open, setOpen] = useState(false);
  const [newPolicy, setNewPolicy] = useState<{
    employee?: Employee;
    inventory?: Inventory;
    quantity: number;
    date: Date;
  }>({
    quantity: 1,
    date: new Date()
  });

  const filteredPolicies = policies?.filter(
    (policy) =>
      policy.inventory.sku.toLowerCase().includes(searchTerm.toLowerCase()) ||
      `${policy.employee.firstName} ${policy.employee.lastName}`.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleAddPolicy = () => {
    const selectedEmployee = employees.find(emp => emp.employeeId === newPolicy.employee?.employeeId);
    const selectedInventory = inventory.find(inv => inv.sku === newPolicy.inventory?.sku);
    
    if (!selectedEmployee || !selectedInventory) {
      console.error("Debe seleccionar un empleado y un artículo del inventario");
      return;
    }

    const newPolicyComplete: Partial<Policy> = {
      policyId: policies.length + 1,
      employee: selectedEmployee,
      inventory: selectedInventory,
      quantity: newPolicy.quantity,
      date: newPolicy.date,
    };
    
    onAddPolicy(newPolicyComplete);
    
    setNewPolicy({
      quantity: 1,
      date: new Date()
    });
    setOpen(false);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Gestión de Pólizas</CardTitle>
        <CardDescription>
          Administra las pólizas de seguros de tus empleados.
        </CardDescription>
        <div className="flex items-center justify-between">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Buscar pólizas..."
              className="pl-8 w-64"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <PlusCircle className="h-4 w-4" /> Nueva Póliza
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Agregar Nueva Póliza</DialogTitle>
                <DialogDescription>
                  Ingresa la información de la nueva póliza.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="employeeId" className="text-right">
                    Empleado
                  </Label>
                  <Select
                    onValueChange={(value) => setNewPolicy({ ...newPolicy, employee: employees.find(emp => emp.employeeId === parseInt(value)) })}
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Seleccionar Empleado" />
                    </SelectTrigger>
                    <SelectContent>
                      {employees?.map((employee) => (
                        <SelectItem key={employee.employeeId} value={employee.employeeId.toString()}>
                          {employee.firstName} {employee.lastName} - {employee.position}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="skuId" className="text-right">
                    Artículo
                  </Label>
                  <Select
                    onValueChange={(value) => {
                      const selected = inventory.find(i => i.sku === value);
                      setNewPolicy(prev => ({...prev, inventory: selected}));
                    }}
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Seleccionar Artículo" />
                    </SelectTrigger>
                    <SelectContent>
                      {inventory?.map((item) => (
                        <SelectItem 
                          key={item.sku} 
                          value={item.sku.toString()}
                        >
                          {item.name} (SKU: {item.sku}, Disponible: {item.quantity})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="quantity" className="text-right">
                    Cantidad
                  </Label>
                  <Input
                    id="quantity"
                    type="number"
                    min="1"
                    value={newPolicy.quantity}
                    onChange={(e) => setNewPolicy({ ...newPolicy, quantity: parseInt(e.target.value) || 1 })}
                    className="col-span-3"
                  />
                </div>

                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="date" className="text-right">
                    Fecha
                  </Label>
                  <Input
                    id="date"
                    type="date"
                    value={newPolicy.date?.toISOString().split("T")[0]}
                    onChange={(e) => setNewPolicy({ ...newPolicy, date: new Date(e.target.value) })}
                    className="col-span-3"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setOpen(false)}>
                  Cancelar
                </Button>
                <Button onClick={handleAddPolicy}>Guardar</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>SKU</TableHead>
              <TableHead>Empleado</TableHead>
              <TableHead>Artículo</TableHead>
              <TableHead>Cantidad</TableHead>
              <TableHead>Fecha</TableHead>
              <TableHead className="text-right">Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredPolicies?.map((policy) => (
              <TableRow key={policy.policyId}>
                <TableCell className="font-medium">{`POL-${policy.policyId.toString().padStart(4, '0')}`}</TableCell>
                <TableCell>{`${policy.employee.firstName} ${policy.employee.lastName}`}</TableCell>
                <TableCell>{policy.inventory.name}</TableCell>
                <TableCell>{policy.quantity}</TableCell>
                <TableCell>{policy.date.toString()}</TableCell>
                <TableCell className="text-right">
                  {onEditPolicy && (
                    <Button variant="ghost" size="sm" onClick={() => onEditPolicy(policy.policyId, policy)}>
                      Editar
                    </Button>
                  )}
                  {onDeletePolicy && (
                    <Button variant="ghost" size="sm" onClick={() => onDeletePolicy(policy.policyId)}>
                      Eliminar
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}