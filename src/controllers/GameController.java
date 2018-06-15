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

package controllers;

import java.util.ArrayList;
import java.util.List;

import engine.api.IModel;
import engine.core.factories.AbstractFactory;
import engine.core.mvc.controller.BaseController;
import entities.ClubCardEntity;
import entities.DiamondCardEntity;
import entities.HeartCardEntity;
import entities.SpadeCardEntity;
import game.core.factories.ModelFactory;
import generated.DataLookup;
import models.CardModel;

public class GameController extends BaseController {

    private final List<CardModel> _cards = new ArrayList<CardModel>();
    
    public GameController() {
        
        ModelFactory modelFactory = AbstractFactory.getFactory(ModelFactory.class);
        
        // Hearts
        for(DataLookup.HEARTS heart : DataLookup.HEARTS.values()) {
            _cards.add(modelFactory.add(new CardModel(new HeartCardEntity(heart))));
        }
        
        // Clubs
        for(DataLookup.CLUBS club : DataLookup.CLUBS.values()) {
            _cards.add(modelFactory.add(new CardModel(new ClubCardEntity(club))));
        }
        
        // Diamonds
        for(DataLookup.DIAMONDS diamond : DataLookup.DIAMONDS.values()) {
            _cards.add(modelFactory.add(new CardModel(new DiamondCardEntity(diamond))));
        }
        
        // Spades
        for(DataLookup.SPADES spade : DataLookup.SPADES.values()) {
            _cards.add(modelFactory.add(new CardModel(new SpadeCardEntity(spade))));
        }
    }
    
    public void nextCard() {
        _cards.get(0).refresh();
    }
    
    @Override protected List<IModel> getControllerModels() {
        List<IModel> models = new ArrayList<IModel>();
        _cards.parallelStream().forEach(z -> models.add(z));
        return models;
    }    
}