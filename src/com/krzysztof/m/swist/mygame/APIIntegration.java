//package com.krzysztof.m.swist.mygame;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.util.Log;
//
//import com.google.android.gms.games.multiplayer.Invitation;
//import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
//import com.google.android.gms.games.multiplayer.Participant;
//import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
//import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
//import com.google.android.gms.games.multiplayer.realtime.Room;
//import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
//import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
//import com.krzysztof.m.swist.utils.basegameutils.BaseGameActivity;
//
//public class APIIntegration  implements RealTimeMessageReceivedListener,
//RoomStatusUpdateListener, RoomUpdateListener,
//OnInvitationReceivedListener{
//	/*
//	 * API INTEGRATION SECTION. This section contains the code that integrates
//	 * the game with the Google Play game services API.
//	 */
//
//	// Debug tag
//	final static boolean ENABLE_DEBUG = true;
//	final static String TAG = "ButtonClicker2000";
//
//	// Request codes for the UIs that we show with startActivityForResult:
//	final static int RC_SELECT_PLAYERS = 10000;
//	final static int RC_INVITATION_INBOX = 10001;
//	final static int RC_WAITING_ROOM = 10002;
//
//	// Room ID where the currently active game is taking place; null if we're
//	// not playing.
//	String roomId = null;
//
//	// Are we playing in multiplayer mode?
//	boolean mMultiplayer = false;
//
//	// The participants in the currently active game
//	ArrayList<Participant> mParticipants = null;
//
//	// My participant ID in the currently active game
//	String mMyId = null;
//
//	// If non-null, this is the id of the invitation we received via the
//	// invitation listener
//	String mIncomingInvitationId = null;
//
//	// Message buffer for sending messages
//	byte[] mMsgBuf = new byte[2];
//
//	// flag indicating whether we're dismissing the waiting room because the
//	// game is starting
//	boolean mWaitRoomDismissedFromCode = false;
//
//	@Override
//	public void onInvitationReceived(Invitation invitation) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onJoinedRoom(int statusCode, Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onLeftRoom(int statusCode, String roomId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onRoomConnected(int statusCode, Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onRoomCreated(int statusCode, Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onConnectedToRoom(Room room) {
//		Log.d(TAG, "onConnectedToRoom.");
//
//		// get room ID, participants and my ID:
//		roomId = room.getRoomId();
//		mParticipants = room.getParticipants();
//		//mMyId = room.getParticipantId(getGamesClient().getCurrentPlayerId());
//
//		// print out the list of participants (for debug purposes)
//		Log.d(TAG, "Room ID: " + roomId);
//		Log.d(TAG, "My ID " + mMyId);
//		Log.d(TAG, "<< CONNECTED TO ROOM>>");
//	}
//
//	@Override
//	public void onDisconnectedFromRoom(Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onP2PConnected(String participantId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onP2PDisconnected(String participantId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeerDeclined(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeerJoined(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeerLeft(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeersConnected(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPeersDisconnected(Room arg0, List<String> arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onRoomAutoMatching(Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onRoomConnecting(Room room) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onRealTimeMessageReceived(RealTimeMessage message) {
//		// TODO Auto-generated method stub
//		
//	}
//}
