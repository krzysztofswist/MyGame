package com.krzysztof.m.swist.mygame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class EndGameFragment extends Fragment implements OnClickListener {

	private int score=0;
	
	
	public void setScore(int score) {
		this.score = score;
	}

	public interface Listener{
		public void onEndGameScreenDismissed();
	}

	private Listener listener = null;
	
	
	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		View v = inflater.inflate(R.layout.fragment_win, container, false);
		v.findViewById(R.id.win_ok_button).setOnClickListener(this);
		((TextView) v.findViewById(R.id.score_display)).setText(String.valueOf(score));
		
		return v;
	}

	@Override
	public void onClick(View arg0) {
		listener.onEndGameScreenDismissed();
	}
	
	
}
