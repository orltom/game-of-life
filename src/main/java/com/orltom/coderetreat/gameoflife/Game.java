package com.orltom.coderetreat.gameoflife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

enum Cell {
	DEAD(l -> l.stream().filter(Cell::isAlive).count() == 3),
	ALIVE(l -> {
		long count = l.stream().filter(Cell::isAlive).count();
		return count == 2 || count == 3;
	});

	Predicate<List<Cell>> rule;

	Cell(Predicate<List<Cell>> rule) {
		this.rule = rule;
	}

	Cell tick(List<Cell> neighbors) {
		return rule.test(neighbors) ? ALIVE : DEAD;
	}

	private boolean isAlive() {
		return this == ALIVE;
	}

	@Override
	public String toString() {
		return ALIVE == this ? "\u25A0" : " ";
	}
}

final class Board {
	private final Cell[][] field;

	Board(Cell[][] field) {
		this.field = field;
	}

	Board tick() {
		Cell[][] newField = new Cell[field.length][field[0].length];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				List<Cell> neighbors = neighbors(i, j);
				newField[i][j] = field[i][j].tick(neighbors);
			}
		}
		return new Board(newField);
	}

	private List<Cell> neighbors(int x, int y) {
		int x1 = max(0, x - 1), x2 = min(field.length - 1, x + 1);
		int y1 = max(0, y - 1), y2 = min(field[0].length - 1, y + 1);
		List<Cell> tmp = new ArrayList<>();
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				if (i != x || j != y) {
					tmp.add(field[i][j]);
				}
			}
		}
		return tmp;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < field.length; i++) {
			sb.append(stream(field[i]).map(Cell::toString).collect(joining(" "))).append("\n");
		}
		return sb.toString();
	}
}

public final class Game {

	public static void main(String[] arr) throws Exception {
		Cell[][] field = new Cell[15][15];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = Cell.DEAD;
			}
		}
		field[0][1] = field[1][2] = field[2][0] = field[2][1] = field[2][2] = Cell.ALIVE;
		run(new Board(field));
	}

	private static void run(Board board) throws InterruptedException, IOException {
		for (int i = 0; i < 30; i++) {
			System.out.print(board);
			board = board.tick();
			Thread.sleep(1000);
		}
	}
}