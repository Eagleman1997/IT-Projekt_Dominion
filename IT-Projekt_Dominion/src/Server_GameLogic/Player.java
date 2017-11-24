package Server_GameLogic;

import java.net.Socket;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Logger;

import Cards.Card;
import Cards.CardName;
import Cards.CardType;
import Messages.Content;
import Messages.Failure_Message;
import Messages.Interaction;
import Messages.Message;
import Messages.PlayerSuccess_Message;
import Messages.UpdateGame_Message;

/**
 * @author Bodo
 * @version 1.0
 * @created 31-Okt-2017 17:08:57
 */
public class Player {

	protected LinkedList<Card> handCards;
	protected LinkedList<Card> playedCards;
	protected Stack<Card> deckPile;
	protected Stack<Card> discardPile;

	protected final int NUM_OF_HANDCARDS = 5;

	protected String playerName;
	protected int actions;
	protected int buys;
	protected int coins;
	protected int moves;
	protected int victoryPoints;
	protected boolean winner;
	protected boolean isFinished;

	protected Game game;
	protected Phase actualPhase;

	protected int counter;

	protected Socket clientSocket;
	private ServerThreadForClient serverThreadForClient;
	
	private final Logger logger = Logger.getLogger("");

	/**
	 * 
	 * @param name
	 */
	public Player(String name, ServerThreadForClient serverThreadForClient) {
		this.deckPile = new Stack<Card>();
		this.discardPile = new Stack<Card>();
		this.handCards = new LinkedList<Card>();
		this.playedCards = new LinkedList<Card>();

		this.playerName = name;
		this.winner = false;

		this.coins = 0;
		this.actions = 0;
		this.buys = 0;
		this.counter = 0;

		this.isFinished = false;

		this.serverThreadForClient = serverThreadForClient;
	}

	/**
	 * Constructor for Bot
	 * 
	 * @param name
	 */
	public Player(String name) {
		this.deckPile = new Stack<Card>();
		this.discardPile = new Stack<Card>();
		this.handCards = new LinkedList<Card>();
		this.playedCards = new LinkedList<Card>();

		this.playerName = name;
		this.winner = false;

		this.coins = 0;
		this.actions = 0;
		this.buys = 0;
		counter = 0;

		this.isFinished = false;
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         Initializes the player to start a move.
	 */
	public void startMove() {
		this.actions = 1;
		this.buys = 1;
		this.coins = 0;
		this.counter = 0;
		this.actualPhase = Phase.Action;
		this.isFinished = false;
	}

	/**
	 * @author Bodo Gruetter
	 *
	 *         allows the current player to play a card and execute this.
	 *
	 * @param the
	 *            played card and the index of this in the handcards.
	 * @return playersuccess message if the game is finished or an updategame
	 *         message if the move is valid. if the move is not valid the
	 *         methods returns a failure message.
	 */
	public Message play(CardName cardName) {
		// Executes the clicked Card, if the player has enough actions
		if (this.getActions() > 0 && this.actualPhase == Phase.Action && this.equals(game.getCurrentPlayer())) {
			int index = this.handCards.indexOf(cardName);
			Card playedCard = this.handCards.remove(index);
			UpdateGame_Message ugmsg = playedCard.executeCard(this);
			this.actions--;
			playedCards.add(playedCard);
			
			// Checks if the game is finished. If it is, it checks the winner and
			// sends it to the opponent.
			if (game.checkGameEnding()) {
				game.checkWinner();

				if (this.winner == true) {
					PlayerSuccess_Message psmsg = new PlayerSuccess_Message();
					psmsg.setSuccess(Content.Won);
					psmsg.setVictoryPoints(this.victoryPoints);

					PlayerSuccess_Message psmsgOpp = new PlayerSuccess_Message();
					psmsgOpp.setSuccess(Content.Lost);
					psmsgOpp.setVictoryPoints(this.victoryPoints);
					this.sendToOpponent(this, psmsgOpp);

					return psmsg;
				} else {
					PlayerSuccess_Message psmsg = new PlayerSuccess_Message();
					psmsg.setSuccess(Content.Lost);
					psmsg.setVictoryPoints(this.victoryPoints);

					PlayerSuccess_Message psmsgOpp = new PlayerSuccess_Message();
					psmsgOpp.setSuccess(Content.Won);
					psmsgOpp.setVictoryPoints(this.victoryPoints);
					this.sendToOpponent(this, psmsgOpp);

					return psmsg;
				}
			}
			
			this.skipPhase();
			this.sendToOpponent(this, ugmsg);
			return ugmsg;
		}

		Failure_Message fmsg = new Failure_Message();
		return fmsg;
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         allows the current player to buy a card and stores it in the
	 *         discard pile.
	 * 
	 * @param the
	 *            buyed card
	 * @return playersuccess message if the game is finished or an updategame
	 *         message if the move is valid. if the move is not valid the
	 *         methods returns a failure message.
	 */
	public Message buy(CardName cardName) {
		Card buyedCard = null;
		UpdateGame_Message ugmsg = new UpdateGame_Message();
		Failure_Message fmsg = new Failure_Message();

		if (Card.getCard(cardName).getCost() <= this.getCoins() && this.getBuys() > 0 && this.actualPhase == Phase.Buy
				&& this.equals(game.getCurrentPlayer())) {

			try {
				// wenn gekauft, noch buys zur verfuegung
				switch (cardName) {
				case Copper:
					buyedCard = this.game.getCopperPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Cellar:
					buyedCard = this.game.getCellarPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Duchy:
					buyedCard = this.game.getDuchyPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Estate:
					buyedCard = this.game.getEstatePile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Gold:
					buyedCard = this.game.getGoldPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Market:
					buyedCard = this.game.getMarketPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Mine:
					buyedCard = this.game.getMinePile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Province:
					buyedCard = this.game.getProvincePile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Remodel:
					buyedCard = this.game.getRemodelPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Silver:
					buyedCard = this.game.getSilverPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Smithy:
					buyedCard = this.game.getSmithyPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Village:
					buyedCard = this.game.getVillagePile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Woodcutter:
					buyedCard = this.game.getWoodcutterPile().pop();
					this.discardPile.push(buyedCard);
					break;
				case Workshop:
					buyedCard = this.game.getWorkshopPile().pop();
					this.discardPile.push(buyedCard);
					break;
				}
				
				this.coins -= buyedCard.getCost();
				this.buys--;
			} catch (EmptyStackException e) {
				this.logger.severe("The buy card stack is empty!");
				return fmsg;
			}

			if (game.checkGameEnding()) {
				game.checkWinner();

				if (this.winner == true) {
					PlayerSuccess_Message psmsg = new PlayerSuccess_Message();
					psmsg.setSuccess(Content.Won);
					psmsg.setVictoryPoints(this.victoryPoints);

					PlayerSuccess_Message psmsgOpp = new PlayerSuccess_Message();
					psmsgOpp.setSuccess(Content.Lost);
					psmsgOpp.setVictoryPoints(this.victoryPoints);
					this.sendToOpponent(this, psmsgOpp);

					return psmsg;
				} else {
					PlayerSuccess_Message psmsg = new PlayerSuccess_Message();
					psmsg.setSuccess(Content.Lost);
					psmsg.setVictoryPoints(this.victoryPoints);

					PlayerSuccess_Message psmsgOpp = new PlayerSuccess_Message();
					psmsgOpp.setSuccess(Content.Won);
					psmsgOpp.setVictoryPoints(this.victoryPoints);
					this.sendToOpponent(this, psmsgOpp);

					return psmsg;
				}
			}

			/**
			 * checks if the buy of the current player is valid, then actualize
			 * the updateGame_Message. else the method returns a
			 * failure_Message.
			 */
			ugmsg.setLog(this.playerName + " bought a " + buyedCard.getCardName() + " Card.");
			// Coins noch abziehen
			ugmsg.setCoins(this.coins);
			// Nur das setzen was sich geaendert hat
			ugmsg.setActions(this.actions);
			// Buys noch abziehen
			ugmsg.setBuys(this.buys);
			// Nur setzen wenn geaendert
			ugmsg.setCurrentPhase(actualPhase);
			ugmsg.setDiscardPileTopCard(this.discardPile.firstElement());
			ugmsg.setDiscardPileCardNumber(this.discardPile.size());
			ugmsg.setBuyedCard(buyedCard);
			ugmsg.setInteractionType(Interaction.EndOfTurn);

			return ugmsg;
		}

		return fmsg;
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         cleans up automatically if a the current player has finished his
	 *         move and switches the player.
	 */
	public Message cleanUp() {
		UpdateGame_Message ugmsg = new UpdateGame_Message();

		this.discard();

		this.draw(this.NUM_OF_HANDCARDS);

		this.setFinished(true);

		this.actualPhase = Phase.Action;

		this.moves++;

		game.switchPlayer();
		ugmsg.setCurrentPlayer(game.getCurrentPlayer().getPlayerName());
		this.actualPhase = Phase.Action;

		return ugmsg;

	}

	/*
	 * Interaction methoden
	 * 
	 * EndOfTurn: Phase auf CleanUp, dem Gegner mitteilen, welche die TopCard
	 * auf dem DiscardPile ist. Nur wenn mehr als eine Karte in der Hand ist
	 * (Abfrage in Buy) InteractionType ueber UpdateGameMessage.
	 */

	private void discard() {
		while (!playedCards.isEmpty()) {
			this.discardPile.push(playedCards.remove());
		}

		while (!handCards.isEmpty()) {
			this.discardPile.push(handCards.remove());
		}
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         If Deckpile is empty, the discard pile fills the deckPile.
	 *         Eventually the deckPiles get shuffled and the player draws the
	 *         number of layed down Cards from deckPile to HandPile.
	 *
	 *         Else If the deckpile size is lower than 5, the rest of deckPiles
	 *         will be drawed and the discard pile fills the deckPile.
	 *         eventually the deckPile get shuffled and the player draws the
	 *         number of layed down Cards in the HandPile.
	 *
	 *         Else if they are enough cards in the deckPile, the player draws
	 *         the number of layed down cards respectively 5 Cards into the
	 *         handPile
	 * 
	 * @param the
	 *            number of cards which should be drawed.
	 * @return an updateGame message
	 */
	public UpdateGame_Message draw(int numOfCards) {

		UpdateGame_Message ugmsg = new UpdateGame_Message();

		for (int i = 0; i < numOfCards; i++) {
			if (!deckPile.isEmpty() && discardPile.isEmpty()) {
				Collections.shuffle(deckPile);
				for (int y = 0; i < numOfCards; i++)
					handCards.add(discardPile.pop());
			} else if (deckPile.isEmpty() && !discardPile.isEmpty()) {
				while (!discardPile.isEmpty())
					deckPile.push(discardPile.pop());
				Collections.shuffle(deckPile);
				for (int y = 0; y < numOfCards; y++)
					handCards.add(deckPile.pop());
			} else if (deckPile.size() < numOfCards) {
				while (!deckPile.isEmpty())
					handCards.add(deckPile.pop());
				while (!discardPile.isEmpty())
					deckPile.push(discardPile.pop());
				Collections.shuffle(deckPile);
				for (int y = 0; y < numOfCards; y++)
					handCards.add(deckPile.pop());
			} else {
				for (int y = 0; y < numOfCards - handCards.size(); y++)
					handCards.add(deckPile.pop());
			}
		}

		ugmsg.setDeckPileCardNumber(this.deckPile.size());
		ugmsg.setDiscardPileCardNumber(this.discardPile.size());
		ugmsg.setNewHandCards(handCards);

		return ugmsg;

	}

	/**
	 * @Bodo Gruetter
	 * 
	 *       Lays the selected card down from Handcards into discardPile and
	 *       counts the number of layed down cards and returns this number
	 * 
	 * @param the
	 *            card which should been layed down
	 * @return the number of layed down cards
	 */
	public int layDown(String cardName) {
		Card layedDownCard = null;
		int index = 0;

		index = this.handCards.indexOf(cardName);
		layedDownCard = this.handCards.remove(index);
		this.discardPile.push(layedDownCard);

		this.counter++;
		return counter;
	}

	/**
	 * @author Bodo Gruetter skips actual phase and goes to the next phase
	 * 
	 * @return an updategamemessage if the skipping works, else an failure
	 *         message
	 */
	public Message skipPhase() {

		UpdateGame_Message ugmsg = new UpdateGame_Message();
		Failure_Message fmsg = new Failure_Message();

		if (this.equals(game.getCurrentPlayer())) {
			switch (this.actualPhase) {
			case Action:
				this.actualPhase = Phase.Buy;
				ugmsg.setCurrentPhase(Phase.Buy);

			case Buy:
				this.actualPhase = Phase.CleanUp;
				ugmsg.setCurrentPhase(Phase.CleanUp);
				this.cleanUp();

			case CleanUp:

			default:
				break;
			}

			return ugmsg;
		}

		return fmsg;
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         allows the current player to count his victory points.
	 */
	public void countVictoryPoints() {
		while (!this.handCards.isEmpty())
			this.deckPile.push(handCards.remove());
		while (!this.playedCards.isEmpty())
			this.deckPile.push(playedCards.remove());
		while (!this.discardPile.isEmpty())
			this.deckPile.push(discardPile.pop());

		Iterator<Card> iter = deckPile.iterator();
		while (iter.hasNext())
			if (iter.next().getType().equals(CardType.Victory)) {
				iter.next().executeCard(this);
			}
	}

	/**
	 * @author Bodo Gruetter
	 * 
	 *         sends an waiting message to the opponent
	 * 
	 * @param the
	 *            sending player and the message which should be send
	 */
	public void sendToOpponent(Player source, Message msg) {
		source.getServerThreadForClient().addWaitingMessages(msg);
	}

	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public Stack<Card> getDeckPile() {
		return deckPile;
	}

	public void setDeckPile(Stack<Card> deckPile) {
		this.deckPile = deckPile;
	}

	public int getBuys() {
		return buys;
	}

	public void setBuys(int buys) {
		this.buys = buys;
	}

	public LinkedList<Card> getHandCards() {
		return handCards;
	}

	public void setHandCards(LinkedList<Card> handCards) {
		this.handCards = handCards;
	}

	public Game getGame() {
		return game;
	}

	/**
	 * 
	 * @param gameThread
	 */
	public void addGame(Game game) {
		this.game = game;
	}

	public Stack<Card> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(Stack<Card> discardPile) {
		this.discardPile = discardPile;
	}

	public LinkedList<Card> getPlayedCards() {
		return playedCards;
	}

	public void setPlayedCards(LinkedList<Card> playedCards) {
		this.playedCards = playedCards;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public Phase getActualPhase() {
		return actualPhase;
	}

	public void setActualPhase(Phase actualPhase) {
		this.actualPhase = actualPhase;
	}

	public ServerThreadForClient getServerThreadForClient() {
		return serverThreadForClient;
	}

	public void setServerThreadForClient(ServerThreadForClient serverThreadForClient) {
		this.serverThreadForClient = serverThreadForClient;
	}

	public void isWinner(boolean winner) {
		this.winner = winner;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}

	public int getMoves() {
		return this.moves;
	}
}// end Player