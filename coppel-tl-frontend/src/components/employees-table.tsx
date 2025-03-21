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
import { Employee } from "@/types";

interface EmployeesTableProps {
  employees: Employee[];
  onAddEmployee: (employee: Partial<Employee>) => void;
  onEditEmployee?: (employeeId: number, employee: Partial<Employee>) => void;
  onDeleteEmployee?: (employeeId: number) => void;
}

const EmployeeInputField = ({ label, id, value, onChange }: any) => (
  <div className="grid grid-cols-4 items-center gap-4">
    <Label htmlFor={id} className="text-right">{label}</Label>
    <Input
      id={id}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="col-span-3"
    />
  </div>
);

export function EmployeesTable({
  employees,
  onAddEmployee,
  onEditEmployee,
  onDeleteEmployee,
}: EmployeesTableProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [newEmployee, setNewEmployee] = useState<Partial<Employee>>({
    firstName: "",
    lastName: "",
    position: "",
    positionValid: false,
  });

  const filteredEmployees = employees.filter((employee) =>
    `${employee.firstName} ${employee.lastName} ${employee.position}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  const handleAddEmployee = () => {
    onAddEmployee(newEmployee);
    setNewEmployee({
      firstName: "",
      lastName: "",
      position: "",
      positionValid: false,
    });
    setIsDialogOpen(false);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Gestión de Empleados</CardTitle>
        <CardDescription>Administra la información de tus empleados.</CardDescription>
        <div className="flex items-center justify-between">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Buscar empleados..."
              className="pl-8 w-64"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <PlusCircle className="h-4 w-4" /> Nuevo Empleado
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Agregar Nuevo Empleado</DialogTitle>
                <DialogDescription>Ingresa la información del nuevo empleado.</DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <EmployeeInputField 
                  label="Nombre" 
                  id="firstName" 
                  value={newEmployee.firstName} 
                  onChange={(value) => setNewEmployee({ ...newEmployee, firstName: value })} 
                />
                <EmployeeInputField 
                  label="Apellido" 
                  id="lastName" 
                  value={newEmployee.lastName} 
                  onChange={(value) => setNewEmployee({ ...newEmployee, lastName: value })} 
                />
                <EmployeeInputField 
                  label="Cargo" 
                  id="position" 
                  value={newEmployee.position} 
                  onChange={(value) => setNewEmployee({ 
                    ...newEmployee, 
                    position: value, 
                    positionValid: value.length > 0 
                  })} 
                />
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsDialogOpen(false)}>Cancelar</Button>
                <Button onClick={handleAddEmployee}>Guardar</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nombre</TableHead>
              <TableHead>Cargo</TableHead>
              <TableHead className="text-right">Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredEmployees.length === 0 ? (
              <TableRow>
                <TableCell colSpan={3} className="text-center text-muted">
                  No hay datos disponibles
                </TableCell>
              </TableRow>
            ) : (
              filteredEmployees.map((employee) => (
                <TableRow key={employee.employeeId}>
                  <TableCell className="font-medium">{`${employee.firstName} ${employee.lastName}`}</TableCell>
                  <TableCell>{employee.position}</TableCell>
                  <TableCell className="text-right">
                    <Button 
                      variant="ghost" 
                      size="sm"
                      onClick={() => onEditEmployee && onEditEmployee(employee.employeeId, employee)}
                    >
                      Editar
                    </Button>
                    {onDeleteEmployee && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        className="text-red-500"
                        onClick={() => onDeleteEmployee(employee.employeeId)}
                      >
                        Eliminar
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
