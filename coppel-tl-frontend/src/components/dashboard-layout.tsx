import { ReactNode } from "react";
import { UserNav } from "@/components/user-nav";

export function DashboardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-background">
      <header className="border-b">
        <div className="flex h-16 items-center px-4">
          <div className="font-bold text-xl">Gestión de Polizas Grupo Coppel</div>
          <div className="ml-auto flex items-center space-x-4">
            <UserNav />
          </div>
        </div>
      </header>
      <main>{children}</main>
    </div>
  );
}