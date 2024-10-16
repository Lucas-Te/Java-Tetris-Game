package data;

import game.Block;
import game.Game;
import game.GameState;
import io.DataHandler;

public class Collision {

	public static boolean collideWithBlock(Block b, int direction) {
		// direction: -1 = left, 0 = down, 1 = right

		switch (direction) {
		case -1:
			if (b.getY() > 0) {
				if (b.getX() > 0) {
					for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
						for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
							if (b.getBounds()[b.getRotation()][i][j] == 1) {
								if (Game.map[b.getX() + i - 1][b.getY() + j] >= 1) {

									return true;
								}
							}
						}
					}

				}
			}
			break;
		case 0:
			if (b.getY() + b.getSize() > 1) {
				if (b.getY() - b.getSize() < 19) {
					try {
						for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
							for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
								if (b.getBounds()[b.getRotation()][i][j] == 1) {

									if (Game.map[b.getX() + i][b.getY() + j + 1] >= 1) {

										Game.spawnNewBlock = true;
										fillBlock(b);

										return true;
									}

								}
							}
						} 
					} catch (Exception e) {
						return false;
					}
				}
			}

			break;
		case 1:
			if (b.getY() > 0) {
				if (b.getX() < 10) {
					for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
						for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
							if (b.getBounds()[b.getRotation()][i][j] == 1) {
								if (Game.map[b.getX() + i + 1][b.getY() + j] >= 1) {

									return true;
								}
							}
						}
					}

				}
			}
			break;
		}

		return false;
	}

	public static boolean collideInRotation(Block b) {
		int rot = b.getRotation() + 1;
		if (rot == 4) {
			rot = 0;
		}
		
		Block block = new Block();
		block.setRotation(rot);
		block.setBounds(b.getBounds());
		block.setSize(b.getSize());
		block.setX(b.getX()-1);
		block.setY(b.getY());
		
		if(collideWithWall(block, 1)) {
			return true;
		}
		block.setX(b.getX()+2);
		if(collideWithWall(block, -1)) {
			return true;
		}

		if (b.getY() > 0) {
			for (int i = 0; i < b.getBounds()[rot].length; i++) {
				for (int j = 0; j < b.getBounds()[rot][i].length; j++) {
					if (b.getBounds()[rot][i][j] == 1) {
						try {
							if (Game.map[b.getX() + i][b.getY() + j] >= 1) {

								return true;
							}
						} catch (Exception e) {
							return true;
						}
					}
				}

			}
		}

		return false;
	}

	public static boolean collideWithWall(Block b, int direction) {
		// direction: -1 = left, 0 = down, 1 = right
		switch (direction) {
		case -1:
			for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
				for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
					if (b.getBounds()[b.getRotation()][i][j] == 1) {
						if (b.getX() + i == 0) {
							return true;
						}
					}
				}
			}
			break;
		case 0:
			for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
				for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
					if (b.getBounds()[b.getRotation()][i][j] == 1) {
						if (b.getY() + j == 19) {
							Game.spawnNewBlock = true;
							fillBlock(b);

							return true;
						}
					}
				}
			}
			break;
		case 1:
			for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
				for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
					if (b.getBounds()[b.getRotation()][i][j] == 1) {
						if (b.getX() + (i + 2) >= 11) {
							return true;
						}
					}
				}
			}
			break;
		}

		return false;
	}

	private static void fillBlock(Block b) {
		try {
			for (int i = 0; i < b.getBounds()[b.getRotation()].length; i++) {
				for (int j = 0; j < b.getBounds()[b.getRotation()][i].length; j++) {
					if (b.getBounds()[b.getRotation()][i][j] == 1) {
						Game.map[b.getX() + i][b.getY() + j] = b.getTypeValue();

					}

				}
			}
		} catch (Exception e) {

		}
		checkLoose();
	}

	public static void checkFullRow(int multiplier) {
		
		int blocksInRow = 0;

		for (int y = Game.map[0].length - 1; y > 0; y--) {			// we go throught the grid game to figure out if there is full line or no , starting from the top of the grid and going below
			for (int x = 0; x < Game.map.length; x++) {

				if (Game.map[x][y] > 0) {
					blocksInRow++;
				}
			}
			if (blocksInRow == 10) {							// when a line is full we erase it and upgrade the score
				Game.scoreToAdd += (100);
				delRow(y, multiplier);
				Game.lines_erased += 1;
				
			} else {
				blocksInRow = 0;
			}

		}
		if(multiplier == 4) {								// if we have 4 four line 
			Game.score += 400;
			Game.scoreToAdd = 0;
		}
		else {
			Game.score += Game.scoreToAdd;
			Game.scoreToAdd = 0;
		}
		

		if (Game.score > Game.highscore) {
			Game.highscore = Game.score;
			DataHandler.save();
		}
	}

	private static void delRow(int row, int multiplier) {

		for (int i = 0; i < Game.map.length; i++) {       // we erase the row which is full
			Game.map[i][row] = 0;
		}
		
		for (int y = row; y > 1; y--) {					// we make fall everything from 1 step
			for (int x = 0; x < Game.map.length; x++) {
				Game.map[x][y] = Game.map[x][y - 1];
			}

		}
		checkFullRow(multiplier + 1);
	}

	private static void checkLoose() {				// We check if the first line is still empty , if not it's gameover
		for (int x = 0; x < Game.map.length; x++) {

			if (Game.map[x][0] > 0) {
				Game.gamestate = GameState.gameover;
			}

		}
	}

}
