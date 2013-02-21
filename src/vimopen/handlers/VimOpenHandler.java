package vimopen.handlers;

import java.lang.reflect.Field;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class VimOpenHandler extends AbstractHandler {
    /**
     * The constructor.
     */
    public VimOpenHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IFile fff = (IFile) window.getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
        if (fff != null) {
            // We got the file, launch vim
            String filePath = fff.getLocation().toOSString();
            runVim(filePath, window);
        } else { // hack !
            IEditorInput input = window.getActivePage().getActiveEditor().getEditorInput();
            try {
                Field ffileStore = input.getClass().getDeclaredField("fileStore");
                ffileStore.setAccessible(true);
                Object fileStore = ffileStore.get(input);
                Field ffilePath = fileStore.getClass().getDeclaredField("filePath");
                ffilePath.setAccessible(true);
                String filePath = (String) ffilePath.get(fileStore);
                // We got the file, launch vim
                if (filePath != null) {
                    runVim(filePath, window);
                }
            } catch (Throwable e) {
                MessageDialog.openError(window.getShell(), "Error", e.getMessage());
            }
        }
        return null;
    }

    public static void runVim(final String filePath, final IWorkbenchWindow window) {
        try {
            Runtime.getRuntime().exec(new String[] {
                    "\"C:\\Program Files (x86)\\Vim\\vim73\\gvim.exe\"", "\"" + filePath + "\""});
        } catch (Throwable e) {
            MessageDialog.openError(window.getShell(), "Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
