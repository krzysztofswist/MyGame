package com.krzysztof.m.swist.mygame;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

public class GameLogic {
	private MainActivity mainActivity;
	private GoogleApiClient googleApiClient;
	private GoogleAPILogic googleAPILogic;
	private final static String TAG = "GameplayFragment";
	private View view;
	private GameType gameType = null;
	private final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 3;
	// Current state of the game:
	private int secondsLeft = -1; // how long until the game ends (seconds)
	private final static int GAME_DURATION = 5; // game duration, seconds.
	private int score = 0; // user's current score
	private int[] textIds = { R.id.score0, R.id.score1, R.id.score2,
			R.id.score3 };
	private String[] scores = null;

	public void setScores(String[] scores) {
		this.scores = scores;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public GameType getGameType() {
		return gameType;
	}

	public int getScore() {
		return score;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void setGoogleApiClient(GoogleApiClient googleApiClient) {
		this.googleApiClient = googleApiClient;
	}

	public void setGoogleAPILogic(GoogleAPILogic googleAPILogic) {
		this.googleAPILogic = googleAPILogic;
	}
	
	

	public void setView(View view) {
		this.view = view;
	}

	void startQuickGame() {
		// quick-start a game with 1 randomly selected opponent
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
				MIN_OPPONENTS, MAX_OPPONENTS, 0);
		RoomConfig.Builder rtmConfigBuilder = RoomConfig
				.builder(googleAPILogic);
		rtmConfigBuilder.setMessageReceivedListener(googleAPILogic);
		rtmConfigBuilder.setRoomStatusUpdateListener(googleAPILogic);
		rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		// switchToScreen(R.id.screen_wait);
		mainActivity.keepScreenOn();
		resetGameVars();
		// getGamesClient().createRoom(rtmConfigBuilder.build());
		Games.RealTimeMultiplayer.create(googleApiClient,
				rtmConfigBuilder.build());
	}

	// Reset game variables in preparation for a new game.
	void resetGameVars() {
		secondsLeft = GAME_DURATION;
		score = 0;
		scores = new String[4];
		// scores.clear();
		// finishedParticipants.clear();
	}

	// Start the gameplay phase of the game.
	void startGame() {
		Log.d(TAG, "Game started. Type:" + gameType);
		resetGameVars();

		googleAPILogic.broadcastScore(false);

		// run the gameTick() method every second to update the game.
		final Handler h = new Handler();
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (secondsLeft <= 0)
					return;
				gameTick();
				h.postDelayed(this, 1000);
			}
		}, 1000);
	}

	// Game tick -- update countdown, check if game ended.
	private void gameTick() {
		if (secondsLeft > 0)
			--secondsLeft;

		// update countdown
		((TextView) view.findViewById(R.id.countdown)).setText("0:"
				+ (secondsLeft < 10 ? "0" : "") + String.valueOf(secondsLeft));

		if (secondsLeft <= 0) {
			// finish game
			view.findViewById(R.id.button_click_me).setVisibility(View.GONE);
			googleAPILogic.broadcastScore(true);
			// setContentView(R.layout.fragment_win);
			mainActivity.onGameEnd();

		}
	}

	// indicates the player scored one point
	public void scoreOnePoint() {
		if (secondsLeft <= 0)
			return; // too late!
		score++;
		updateScoreDisplay();
		//updatePeerScoresDisplay();

		// broadcast our new score to our peers
		// broadcastScore(false);
	}

	private void updateScoreDisplay() {
		((TextView) view.findViewById(R.id.my_score))
				.setText(formatScore(score));
		int i = 0;

		for (int id : textIds) {

			((TextView) view.findViewById(id))
					.setText(scores[i] == null ? "null" : scores[i]);
			i++;
		}

	}

	// formats a score as a three-digit number
	public String formatScore(int i) {
		if (i < 0)
			i = 0;
		String s = String.valueOf(i);
		return s.length() == 1 ? "00" + s : s.length() == 2 ? "0" + s : s;
	}

}
