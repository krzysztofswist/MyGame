package com.krzysztof.m.swist.mygame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

public class GoogleAPILogic implements RealTimeMessageReceivedListener,
		RoomStatusUpdateListener, RoomUpdateListener,
		OnInvitationReceivedListener {

	final static String TAG = "MyGame:" + GoogleAPILogic.class.getSimpleName();
	private final static int RC_WAITING_ROOM = 10002;
	// Room ID where the currently active game is taking place; null if we're
	// not playing.
	String roomId = null;

	// Are we playing in multiplayer mode?
	// boolean mMultiplayer = false;

	// The participants in the currently active game
	ArrayList<Participant> participants = null;

	// Score of other participants. We update this as we receive their scores
	// from the network.
	Map<String, Integer> participantScore = new HashMap<String, Integer>();

	// Participants who sent us their final score.
	Set<String> finishedParticipants = new HashSet<String>();

	// My participant ID in the currently active game
	String myId = null;

	// If non-null, this is the id of the invitation we received via the
	// invitation listener
	String incomingInvitationId = null;

	// Message buffer for sending messages
	byte[] msgBuf = new byte[2];

	// flag indicating whether we're dismissing the waiting room because the
	// game is starting
	boolean waitRoomDismissedFromCode = false;

	private MainMenuFragment mainMenuFragment;
	private MainActivity mainActivity;
	private GameLogic gameLogic;
	private GoogleApiClient googleApiClient;

	public void setGoogleApiClient(GoogleApiClient googleApiClient) {
		this.googleApiClient = googleApiClient;
	}

	public void setMainMenuFragment(MainMenuFragment mainMenuFragment) {
		this.mainMenuFragment = mainMenuFragment;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}

	@Override
	public void onInvitationRemoved(String invitationId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onP2PConnected(String participantId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onP2PDisconnected(String participantId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerDeclined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerLeft(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeersConnected(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeersDisconnected(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoomAutoMatching(Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoomConnecting(Room room) {
		// TODO Auto-generated method stub

	}

	// Handle the result of the "Select players UI" we launched when the user
	// clicked the
	// "Invite friends" button. We react by creating a room with those players.
	void handleSelectPlayersResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** select players UI cancelled, " + response);
			mainActivity.switchToFragment(mainMenuFragment);
			return;
		}

		Log.d(TAG, "Select players UI succeeded.");

		// get the invitee list
		final ArrayList<String> invitees = data
				.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);
		Log.d(TAG, "Invitee count: " + invitees.size());

		// get the automatch criteria
		Bundle autoMatchCriteria = null;
		int minAutoMatchPlayers = data.getIntExtra(
				GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAutoMatchPlayers = data.getIntExtra(
				GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
			autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
					minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
		}

		// create the room
		Log.d(TAG, "Creating room...");
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
		rtmConfigBuilder.addPlayersToInvite(invitees);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(this);
		if (autoMatchCriteria != null) {
			rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		}
		// switchToScreen(R.id.screen_wait);
		mainActivity.keepScreenOn();
		gameLogic.resetGameVars();
		// getGamesClient().createRoom(rtmConfigBuilder.build());
		Games.RealTimeMultiplayer.create(googleApiClient,
				rtmConfigBuilder.build());
		Log.d(TAG, "Room created, waiting for it to be ready...");
	}

	// Handle the result of the invitation inbox UI, where the player can pick
	// an invitation
	// to accept. We react by accepting the selected invitation, if any.
	void handleInvitationInboxResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
			mainActivity.switchToFragment(mainMenuFragment);
			return;
		}

		Log.d(TAG, "Invitation inbox UI succeeded.");
		Invitation inv = data.getExtras().getParcelable(
				GamesClient.EXTRA_INVITATION);

		// accept invitation
		acceptInviteToRoom(inv.getInvitationId());
	}

	// Accept the given invitation.
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Log.d(TAG, "Accepting invitation: " + invId);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
		// switchToScreen(R.id.screen_wait);
		mainActivity.keepScreenOn();
		gameLogic.resetGameVars();
		Games.RealTimeMultiplayer.join(googleApiClient,
				roomConfigBuilder.build());
	}

	// Called when we receive a real-time message from the network.
	// Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
	// indicating
	// whether it's a final or interim score. The second byte is the score.
	// There is also the
	// 'S' message, which indicates that the game should start.
	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] buf = rtm.getMessageData();
		String sender = rtm.getSenderParticipantId();
		Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);

		if (buf[0] == 'F' || buf[0] == 'U') {
			// score update.
			int existingScore = participantScore.containsKey(sender) ? participantScore
					.get(sender) : 0;
			int thisScore = (int) buf[1];
			if (thisScore > existingScore) {
				// this check is necessary because packets may arrive out of
				// order, so we
				// should only ever consider the highest score we received, as
				// we know in our
				// game there is no way to lose points. If there was a way to
				// lose points,
				// we'd have to add a "serial number" to the packet.
				participantScore.put(sender, thisScore);
			}

			// update the scores on the screen
			updatePeerScoresDisplay();

			// if it's a final score, mark this participant as having finished
			// the game
			if ((char) buf[0] == 'F') {
				finishedParticipants.add(rtm.getSenderParticipantId());
			}
		}
		// else if (buf[0] == 'S') {
		// // someone else started to play -- so dismiss the waiting room and
		// // get right to it!
		// Log.d(TAG, "Starting game because we got a start message.");
		// dismissWaitingRoom();
		// SwitchToFragment(gameplayFragment);
		// }
	}

	// Called when room has been created
	// @Override
	public void onRoomCreated(int statusCode, Room room) {
		Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
		if (statusCode != GamesClient.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
			mainActivity.showGameError();
			return;
		}

		// show the waiting room UI
		showWaitingRoom(room);
	}

	// Called when room is fully connected.
	// @Override
	public void onRoomConnected(int statusCode, Room room) {
		Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
		if (statusCode != GamesClient.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
			mainActivity.showGameError();
			return;
		}
		updateRoom(room);
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
		if (statusCode != GamesClient.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
			mainActivity.showGameError();
			return;
		}

		// show the waiting room UI
		showWaitingRoom(room);
	}

	// Called when we are connected to the room. We're not ready to play yet!
	// (maybe not everybody
	// is connected yet).
	@Override
	public void onConnectedToRoom(Room room) {
		Log.d(TAG, "onConnectedToRoom.");

		// get room ID, participants and my ID:
		roomId = room.getRoomId();
		participants = room.getParticipants();
		myId = room.getParticipantId(Games.Players
				.getCurrentPlayerId(googleApiClient));

		// print out the list of participants (for debug purposes)
		Log.d(TAG, "Room ID: " + roomId);
		Log.d(TAG, "My ID " + myId);
		Log.d(TAG, "<< CONNECTED TO ROOM>>");
	}

	// Called when we've successfully left the room (this happens a result of
	// voluntarily leaving
	// via a call to leaveRoom(). If we get disconnected, we get
	// onDisconnectedFromRoom()).
	// @Override
	public void onLeftRoom(int statusCode, String roomId) {
		// we have left the room; return to main screen.
		Log.d(TAG, "onLeftRoom, code " + statusCode);
		mainActivity.switchToFragment(mainMenuFragment);
	}

	// Called when we get disconnected from the room. We return to the main
	// screen.
	// @Override
	public void onDisconnectedFromRoom(Room room) {
		roomId = null;
		mainActivity.showGameError();
	}

	// Called when we get an invitation to play a game. We react by showing that
	// to the user.
	@Override
	public void onInvitationReceived(Invitation invitation) {
		// We got an invitation to play a game! So, store it in
		// mIncomingInvitationId
		// and show the popup on the screen.
		incomingInvitationId = invitation.getInvitationId();
		((TextView) mainActivity.findViewById(R.id.incoming_invitation_text))
				.setText(invitation.getInviter().getDisplayName() + " "
						+ mainActivity.getString(R.string.is_inviting_you));
		// switchToScreen(mCurScreen); // This will show the invitation popup
	}

	// Leave the room.
	void leaveRoom() {
		Log.d(TAG, "Leaving room.");
		// mSecondsLeft = 0;
		mainActivity.stopKeepingScreenOn();
		if (roomId != null) {
			Games.RealTimeMultiplayer.leave(googleApiClient, this, roomId);
			roomId = null;
			mainActivity.switchToFragment(mainMenuFragment);
			// switchToScreen(R.id.screen_wait);
		} else {
			mainActivity.switchToFragment(mainMenuFragment);
			// switchToMainScreen();
		}
	}

	// Show the waiting room UI to track the progress of other players as they
	// enter the
	// room and get connected.
	void showWaitingRoom(Room room) {
		waitRoomDismissedFromCode = false;

		// minimum number of players required for our game
		final int MIN_PLAYERS = 2;
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(
				googleApiClient, room, MIN_PLAYERS);

		// show waiting room UI
		mainActivity.startActivityForResult(i, RC_WAITING_ROOM);
	}

	// Forcibly dismiss the waiting room UI (this is useful, for example, if we
	// realize the
	// game needs to start because someone else is starting to play).
	void dismissWaitingRoom() {
		waitRoomDismissedFromCode = true;
		mainActivity.finishActivity(RC_WAITING_ROOM);
	}

	void updateRoom(Room room) {
		participants = room.getParticipants();
		updatePeerScoresDisplay();
	}

	// Broadcast my score to everybody else.
	void broadcastScore(boolean finalScore) {
		if (gameLogic.getGameType() != GameType.MULTIPLAYER)
			return; // playing single-player mode

		// First byte in message indicates whether it's a final score or not
		msgBuf[0] = (byte) (finalScore ? 'F' : 'U');

		// Second byte is the score.
		msgBuf[1] = (byte) gameLogic.getScore();

		// Send to every other participant.
		for (Participant p : participants) {
			if (p.getParticipantId().equals(myId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			if (finalScore) {
				// final score notification must be sent via reliable message
				Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient,
						null, msgBuf, roomId, p.getParticipantId());
			} else {
				// it's an interim score notification, so we can use unreliable
				Games.RealTimeMultiplayer.sendUnreliableMessage(
						googleApiClient, msgBuf, roomId, p.getParticipantId());
			}
		}
	}

	// updates the screen with the scores from our peers
	private void updatePeerScoresDisplay() {
		// gameplayFragment.setScores(participantScore);
		// gameplayFragment.updateScoresDisplay();
		String scores[] = new String[4];
		// ((TextView)
		// gameplayFragment.getView().findViewById(R.id.score0)).setText(gameplayFragment.formatScore(gameplayFragment.getScore())
		// + " - Me");
		scores[0] = gameLogic.formatScore(gameLogic.getScore()) + " - Me";
		int[] arr = { R.id.score1, R.id.score2, R.id.score3 };
		int i = 1;

		if (roomId != null) {
			for (Participant p : participants) {
				String pid = p.getParticipantId();
				if (pid.equals(myId))
					continue;
				if (p.getStatus() != Participant.STATUS_JOINED)
					continue;
				int score = participantScore.containsKey(pid) ? participantScore
						.get(pid) : 0;
				((TextView) mainActivity.findViewById(arr[i]))
						.setText(gameLogic.formatScore(score) + " - "
								+ p.getDisplayName());
				scores[i] = gameLogic.formatScore(score) + " - "
						+ p.getDisplayName();
				++i;
			}
		}

		for (; i < scores.length; ++i) {
			scores[i] = "";
			 ((TextView) mainActivity.findViewById(arr[i])).setText("");
		}
		gameLogic.setScores(scores);
	}
}
