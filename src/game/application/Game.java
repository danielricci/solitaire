/**
 * Daniel Ricci <thedanny09@icloud.com>
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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.UIManager;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.core.system.EngineProperties;
import framework.core.system.EngineProperties.Property;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.menu.AboutMenuItem;
import game.menu.DeckMenuItem;
import game.menu.ExitMenuItem;
import game.menu.NewGameMenuItem;
import game.menu.OptionsMenuItem;
import game.menu.UndoMenuItem;
import game.views.FoundationView;
import game.views.PileView;
import game.views.TalonView;

import resources.LocalizationStrings;

public final class Game extends Application {

    /**
     * Constructs a new instance of this class type
     * 
     * @param isDebug The debug mode flag
     */
    private Game(boolean isDebug) {
        super(isDebug);
        setMinimumSize(new Dimension(620, 436));
        setLocationRelativeTo(null);
        setAlwaysOnTop(isDebug);
        setIconImage(Localization.instance().getLocalizedData(LocalizationStrings.GAME_ICON));
        
        if(isDebug) {
            addKeyListener(new KeyAdapter() {
                @Override public void keyReleased(KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.VK_F1) {
                        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
                        System.out.println(viewFactory.get(TalonView.class).toString());
                        List<PileView> pileViews =  viewFactory.getAll(PileView.class);
                        for(int i = pileViews.size() - 1; i >= 0; --i) {
                            System.out.println(pileViews.get(i));
                        }
                        List<FoundationView> foundationViews =  viewFactory.getAll(FoundationView.class);
                        for(int i = foundationViews.size() - 1; i >= 0; --i) {
                            System.out.println(foundationViews.get(i));
                        }
                        
                        OptionsPreferences options = new OptionsPreferences();
                        options.load();
                        System.out.println(options);
                    }
                }
            });
        }
    }
    
    /**
     * Main entrypoint method
     * 
     * @param args The arguments associated to the application entry point
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                boolean debugMode = false;
                for(String arg : args) {
                    if(arg.trim().equalsIgnoreCase("debug")) {
                        debugMode = true;
                        break;
                    }
                }
                new Game(debugMode);
            }
        });
    }
    
    @Override protected void onBeforeEngineDataInitialized() {
        EngineProperties.instance().setProperty(Property.DATA_PATH_XML, "/generated/tilemap.xml");
        EngineProperties.instance().setProperty(Property.DATA_PATH_SHEET, "/generated/tilemap.png");
        EngineProperties.instance().setProperty(Property.LOG_DIRECTORY,  System.getProperty("user.home") + File.separator + "desktop" + File.separator);
        EngineProperties.instance().setProperty(Property.LOCALIZATION_PATH_CVS, "resources/localization.csv");
        EngineProperties.instance().setProperty(Property.ENGINE_OUTPUT, Boolean.toString(true));
        EngineProperties.instance().setProperty(Property.SUPPRESS_SIGNAL_REGISTRATION_OUTPUT, Boolean.toString(true));
        EngineProperties.instance().setProperty(Property.DISABLE_TRANSLATIONS_PLACEHOLDER, Boolean.toString(true));
    }

    @Override protected void onWindowInitialized() {
        super.onWindowInitialized();

        
        // Set the title
        setTitle(Localization.instance().getLocalizedString(LocalizationStrings.TITLE));

        
        // Always show mnemonics in the menu system
        UIManager.put("Button.showMnemonics", Boolean.TRUE);
        
        // Game Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(LocalizationStrings.GAME), KeyEvent.VK_G)
        .addMenuItem(NewGameMenuItem.class)
        .addSeparator()
        .addMenuItem(UndoMenuItem.class)
        .addMenuItem(DeckMenuItem.class)
        .addMenuItem(OptionsMenuItem.class)
        .addSeparator()
        .addMenuItem(ExitMenuItem.class);
                
        // Help Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(LocalizationStrings.HELP), KeyEvent.VK_H)
        .addMenuItem(AboutMenuItem.class);

        // Perform a new game programatically
        MenuBuilder.search(getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
    }
}