package ttdd;

import ttdd.player.view.*;
import javax.swing.SwingUtilities;
import javax.swing.*;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class StartApplicationCls {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
            // Establecer el look and feel de Windows
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
		System.out.println("demo");

		try {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					PlayerGUI playerGUI = new PlayerGUI();
					playerGUI.setVisible(true);
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
