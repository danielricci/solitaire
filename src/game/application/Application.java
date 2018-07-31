/**
 * Daniel Ricci <thedanny09@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package game.application;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.AbstractButton;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.MenuBuilder;
import framework.core.system.AbstractApplication;
import framework.core.system.EngineProperties;
import framework.core.system.EngineProperties.Property;
import framework.utils.globalisation.Localization;

import game.menu.AboutMenuItem;
import game.menu.DeckMenuItem;
import game.menu.ExitMenuItem;
import game.menu.NewGameMenuItem;
import game.menu.OptionsMenuItem;
import game.menu.UndoMenuItem;
import game.views.GameView;

import resources.LocalizedStrings;

public final class Application extends AbstractApplication {

    public Application() {
        setMinimumSize(new Dimension(620, 436));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {

                // Get the debug mode state based on the arguments passed into the application
                boolean debugMode = false;
                for(String arg : args) {
                    if(arg.trim().equalsIgnoreCase("-debug")) {
                        debugMode = true;
                        break;
                    }
                }

                // Initialize the application
                Application.initialize(Application.class, debugMode);
            }
        });
    }
    
    public void restartApplication() {
        AbstractFactory.clearFactories();
        this.removeAll();
    }

    private void populateMenu() {
        
        // Game Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString("Game"))
        .addMenuItem(NewGameMenuItem.class)
        .addSeparator()
        .addMenuItem(UndoMenuItem.class)
        .addMenuItem(DeckMenuItem.class)
        .addMenuItem(OptionsMenuItem.class)
        .addSeparator()
        .addMenuItem(ExitMenuItem.class);
                
        // Help Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString("Help"))
        .addMenuItem(AboutMenuItem.class);
    }

    @Override protected void onBeforeEngineDataInitialized() {
        EngineProperties.instance().setProperty(Property.DATA_PATH_XML, "/generated/tilemap.xml");
        EngineProperties.instance().setProperty(Property.DATA_PATH_SHEET, "/generated/tilemap.png");
        EngineProperties.instance().setProperty(Property.LOG_DIRECTORY,  System.getProperty("user.home") + File.separator + "desktop" + File.separator);
        EngineProperties.instance().setProperty(Property.ENGINE_OUTPUT, "true");
        EngineProperties.instance().setProperty(Property.LOCALIZATION_PATH_CVS, "resources/localization_solitaire.csv");
        EngineProperties.instance().setProperty(Property.SUPPRESS_SIGNAL_REGISTRATION_OUTPUT, "true");
    }

    @Override protected void onWindowInitialized() {
        super.onWindowInitialized();

        // Set the title
        setTitle(Localization.instance().getLocalizedString(LocalizedStrings.Title));

        // Populate the menu system
        populateMenu();

        // New Game
        MenuBuilder.search(getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
    }
}