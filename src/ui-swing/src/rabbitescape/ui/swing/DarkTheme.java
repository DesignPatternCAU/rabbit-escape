package rabbitescape.ui.swing;

import javax.swing.*;
import java.awt.*;

public class DarkTheme extends Theme
{
    private static Theme uniqueInstance = new DarkTheme();

    private DarkTheme() {
        backgroundColor = Color.LIGHT_GRAY;
    }

    @Override
    public void setSideMenuColor( SideMenu sideMenu )
    {
        sideMenu.setPanelBackground( backgroundColor );
    }

    @Override
    public Theme getOppositeTheme()
    {
        return BrightTheme.getInstance();
    }

    @Override
    public void setColorMenuUi(
        Container contentPane,
        JScrollPane scrollPane,
        JPanel menuPanel
    )
    {
        contentPane.setBackground( backgroundColor );
        scrollPane.setBackground( backgroundColor );
        menuPanel.setBackground( backgroundColor );
    }

    public static Theme getInstance() {
        return uniqueInstance;
    }
}
