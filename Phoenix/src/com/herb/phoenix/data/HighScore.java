package com.herb.phoenix.data;

public class HighScore implements Comparable<HighScore>
{
	public final String name;
	public final int score, level;
	
	public HighScore(String name, int level, int score)
	{
		this.name = name;
		this.level = level;
		this.score = score;
	}

	@Override
	public int compareTo(HighScore o)
	{
		return this.score - o.score;
	}
}
