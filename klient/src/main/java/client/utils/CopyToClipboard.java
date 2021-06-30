package client.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class CopyToClipboard {
    public static void makeCopy(String str){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(str);
        clipboard.setContent(content);
    }
}
