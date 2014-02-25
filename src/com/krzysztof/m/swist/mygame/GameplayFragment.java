package com.krzysztof.m.swist.mygame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button; 

public class GameplayFragment extends Fragment implements OnClickListener {

	GameLogic gameLogic;

	// Participants who sent us their final score.
	// Set<String> finishedParticipants = new HashSet<String>();

	public interface Listener {
		public void onGameEnd();
	}

	Listener listener = null;

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	

	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gameplay, container,
				false);

		((Button) view.findViewById(R.id.button_click_me))
				.setOnClickListener(this);
		gameLogic.setView(view);
		gameLogic.startGame();
		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case (R.id.button_click_me):
			gameLogic.scoreOnePoint();
			break;
		}

	}
	
	
}
