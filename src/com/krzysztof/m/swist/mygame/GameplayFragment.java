package com.krzysztof.m.swist.mygame;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GameplayFragment extends Fragment implements OnClickListener {

	private final static String TAG = "GameplayFragment";
	private View view;
	private GameType gameType = null;
	// Current state of the game:
	private int secondsLeft = -1; // how long until the game ends (seconds)
	private final static int GAME_DURATION = 30; // game duration, seconds.
	private int score = 0; // user's current score
	private int[] textIds = { R.id.score0, R.id.score1, R.id.score2,
			R.id.score3 };
	private String[] scores = new String[4];

	// Participants who sent us their final score.
	// Set<String> finishedParticipants = new HashSet<String>();

	MainActivity mainActivity = null;

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void setScores(String[] scores) {
		this.scores = scores;
	}

	public View getView() {
		return view;
	}

	public int getScore() {
		return score;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public GameType getGameType() {
		return gameType;
	}

	public interface Listener {
		public void onGameEnd();
	}

	Listener listener = null;

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_gameplay, container, false);

		((Button) view.findViewById(R.id.button_click_me))
				.setOnClickListener(this);
		startGame();

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case (R.id.button_click_me):
			scoreOnePoint();
			break;
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		UpdateUI();
	}

	// Start the gameplay phase of the game.
	private void startGame() {
		Log.d(TAG, "Game started. Type:" + gameType);
		resetGameVars();

		mainActivity.broadcastScore(false);

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
			mainActivity.broadcastScore(true);
			// setContentView(R.layout.fragment_win);
			listener.onGameEnd();

		}
	}

	public void UpdateUI() {

	}

	// indicates the player scored one point
	private void scoreOnePoint() {
		if (secondsLeft <= 0)
			return; // too late!
		score++;
		updateScoreDisplay();
		// updatePeerScoresDisplay();

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

	// Reset game variables in preparation for a new game.
	void resetGameVars() {
		secondsLeft = GAME_DURATION;
		score = 0;
		scores = new String[4];
		// scores.clear();
		// finishedParticipants.clear();
	}

}
