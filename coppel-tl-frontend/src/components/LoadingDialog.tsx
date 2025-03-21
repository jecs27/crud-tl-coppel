import { Dialog, DialogContent } from "@/components/ui/dialog"
import { DialogTitle } from "@radix-ui/react-dialog"

interface LoadingDialogProps {
  isOpen: boolean
  message?: string
}

export function LoadingDialog({ isOpen, message = "Cargando..." }: LoadingDialogProps) {
  return (
    <Dialog open={isOpen}>
      <DialogContent className="bg-transparent border-none shadow-none">
        <div className="flex flex-col items-center justify-center space-y-6">
          <div className="relative">
            <div className="h-16 w-16 animate-spin rounded-full border-8 border-primary border-t-transparent"></div>
            <div className="absolute top-0 left-0 h-16 w-16 animate-ping rounded-full border-8 border-primary opacity-20"></div>
          </div>
          <div className="text-center">
            <DialogTitle className="text-xl font-semibold mb-2">
              {message}
            </DialogTitle>
            <p className="text-sm text-muted-foreground">
              Por favor espere mientras procesamos su solicitud...
            </p>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}