import java.util.ArrayList;

public class ParkerStrategy extends MonopolyPlayer{
	MonopolyBoard myBoard = null;
	ParkerStrategy(int id, MonopolyBoard bigBoard) {
		super(id);
		myBoard = bigBoard;
	}
	/**
	 * Determines if the strategy should buy houses,
	 * stay in/escape jail, mortgage
	 */
	public void beforeRoll(int numOwned){
		beforeRollInJail(numOwned);
		determineBuyingHouses();
	}
	private void determineBuyingHouses(){
		for(PropertyCard property : PlayerMonopolyProperties)
			if(property.ableToBuyHouses && new House(property.name(), property.numHouses()+1).getPrice() < money)
				property.buyHouse();
	}
	private void beforeRollInJail(int numOwned){
		//total number of properties: 27
		//could be worth it to mortgage property in order to pay for bail
		if(jail && numOwned < 25){
			if(getOutOfJailFree>0){
				getOutOfJailFree--;
				setJail(false);
			}else if(money > 50){
				money = money - 50;
				setJail(false);
			}
		}
	}
	/**
	 * Determines if the strategy should 
	 * buy properties, mortgage
	 * The boolean returned determines if the player will try to buy the property or not
	 */
	public boolean afterRoll(MonopolySlot slot){
		PropertyCard property;
		if(slot instanceof PropertyCard){
			property = (PropertyCard) slot;
			determineMortgage(property);
			boolean buy = determineBuyProperty(property);
			return buy;
		}
		//Should we be able to buy the property we land on and trade it in the same turn?
		determineTrading();
		return false;
	}
	private void determineTrading(){
		String[] types = {"PURPLE", "LIGHTGREEN", "VIOLET", "ORANGE",
				"RED", "YELLOW", "DARKGREEN", "DARKBLUE", "UTILITIES", "RAILROAD"};
		boolean shouldTrade = false;
		int chosenTraderID = 0;
		int chosenTradeeID = 0;
		PropertyCard chosenTraderProperty = null;
		PropertyCard chosenTradeeProperty = null;
		
		for(int i = 0; i < types.length; i++)
			if(propertiesNeededForMonopoly(types[i]) == 1){
				for(MonopolyPlayer player : myBoard.getPlayers()){
					if(player.playerID != playerID)
						//CHANGE THIS. THROWS EXCEPTION BECAUDSE IT IS BEING ITERATED WHILE BEING MODIFIED
						for(PropertyCard property : player.playerProperties()) {
							if(property.getColor().equals(types[i])){
//								myBoard.trading(playerID, player.playerID, 
//										player.tradeableProperty(PlayerMonopolyProperties), property);
								chosenTraderID = playerID;
								chosenTradeeID = player.playerID;
								chosenTraderProperty = player.tradeableProperty(PlayerMonopolyProperties);
								chosenTradeeProperty = property;
							}
						}
					System.out.println("chosenTraderID: " + chosenTraderID);
					System.out.println("chosenTradeeID: " + chosenTradeeID);
					myBoard.trading(chosenTraderID, chosenTradeeID, chosenTraderProperty, chosenTradeeProperty);
				}
			}
	}
	private boolean determineBuyProperty(PropertyCard property){
		if(money > property.cost())
			return true;
		else
			return false;
	}
	private int potentialMortgage(){
		int mortgageValue = 0;
		for(PropertyCard property : PlayerMonopolyProperties)
			mortgageValue = mortgageValue + property.getMortgageValue();
		return mortgageValue;
	}
	/**
	 * Mortgage property to buy property
	 */
	private void determineMortgage(PropertyCard property) {
		if(!property.owned() && property.cost() > money && property.cost() > money+potentialMortgage()){
			int counter = 0;
			while(property.cost() > money){
				nthCard(counter).mortgage();;
				counter++;
			}
		}
	}
}