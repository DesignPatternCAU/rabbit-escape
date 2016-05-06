package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.config.TapTimer;
import rabbitescape.engine.menu.LevelsList.LevelSetInfo;
import rabbitescape.engine.menu.MenuItem.Type;
import rabbitescape.engine.util.Util.IdxObj;

public class MenuDefinition
{
    static
    {
        TapTimer.checkEnv();
    }
    
    public static final LevelsList allLevels = new LevelsList(
        new LevelSetInfo( "Easy",     "01_easy",     null, false ),
        new LevelSetInfo( "Medium",   "02_medium",   null, false ),
        new LevelSetInfo( "Hard",     "03_hard",     null, false ),
        new LevelSetInfo( "Outdoors", "04_outdoors", null, false ),
        new LevelSetInfo( "Arcade",   "05_arcade",   null, false ),
        new LevelSetInfo( "Development", "development", null, true ),
        new LevelSetInfo( "Staging",  "staging",     null, true)
    );

    public static Menu mainMenu(
        LevelsCompleted levelsCompleted,
        LevelsList loadedLevels,
        boolean includeLoadLevel
    )
    {
        return menu(
            "Welcome to Rabbit Escape!",
            item(
                "Start Game",
                menu(
                    "Choose a set of levels:",
                    items( levelsCompleted, loadedLevels )
                ),
                true,
                false
            ),
            item( "About", Type.ABOUT, true, false ),
            maybeItem(
                includeLoadLevel,
                "Custom Levels",
                menu(
                    "Get from file or network",
                    item( "Load Level", Type.LOAD, true, false ),
                    item( "GitHub Issue", Type.GITHUB_ISSUE, true, false )
                ),
                true,
                false
            ),
            item( "Quit", Type.QUIT,  true, false )
        );
    }

    private static MenuItem[] items(
        LevelsCompleted levelsCompleted,
        LevelsList loadedLevels
    )
    {
        MenuItem[] ret = new MenuItem[ loadedLevels.size() ];
        for ( IdxObj<LevelSetInfo> setI : enumerate( loadedLevels ) )
        {
            LevelSetInfo set = setI.object;
            ret[setI.index] = item(
                set.name,
                new LevelsMenu( set.dirName, loadedLevels, levelsCompleted ),
                true,
                set.hidden
            );
        }
        return ret;
    }
}
