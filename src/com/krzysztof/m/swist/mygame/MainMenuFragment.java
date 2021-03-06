package com.krzysztof.m.swist.mygame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MainMenuFragment extends Fragment implements OnClickListener {

	public interface Listener {
		public void onStartGameRequested(GameType gameType);

		public void onShowAchievementsRequested();

		public void onShowLeaderboardsRequested();

		public void onSignInButtonClicked();

		public void onSignOutButtonClicked();

		public void onInvatePlayersButtonClicked();

		public void onSeeInvitationsButtonClicked();
	}

	private boolean showSignIn = true;
	private Listener listener = null;

	public boolean isShowSignIn() {
		return showSignIn;
	}

	public void setShowSignIn(boolean showSignIn) {
		this.showSignIn = showSignIn;
		UpdateUI();
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_quick_game:
			listener.onStartGameRequested(GameType.MULTIPLAYER);
			break;
		case R.id.button_single_player:
			listener.onStartGameRequested(GameType.SINGLEPLAYER);
			break;
		case R.id.button_invite_players:
			listener.onInvatePlayersButtonClicked();
			break;
		case R.id.button_sign_in:
			listener.onSignInButtonClicked();
			break;
		case R.id.button_sign_out:
			listener.onSignOutButtonClicked();
			break;
		case R.id.button_see_invitations:
			listener.onSeeInvitationsButtonClicked();
			break;
		case R.id.button_show_achievements:
			listener.onShowAchievementsRequested();
			break;
		case R.id.button_show_leaderboards:
			listener.onShowLeaderboardsRequested();
			break;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mainmenu, container,
				false);
		final int[] CLICKABLES = new int[] { R.id.button_sign_in,
				R.id.button_single_player, R.id.button_invite_players,
				R.id.button_quick_game, R.id.button_see_invitations,
				R.id.button_sign_out, R.id.button_show_achievements,
				R.id.button_show_leaderboards };
		for (int id : CLICKABLES) {
			view.findViewById(id).setOnClickListener(this);
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		UpdateUI();
	}

	private void UpdateUI() {
		if (getActivity() == null)
			return;

		getActivity().findViewById(R.id.sign_in_bar).setVisibility(
				showSignIn ? View.VISIBLE : View.GONE);
		getActivity().findViewById(R.id.sign_out_bar).setVisibility(
				showSignIn ? View.GONE : View.VISIBLE);
	}

}
