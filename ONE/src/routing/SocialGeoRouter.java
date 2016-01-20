/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

import movement.Path;
import util.Tuple;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;

/**
 * Implementation of Social Geolocation based DTN routing scheme
 */
public class SocialGeoRouter extends ActiveRouter {
	/** identifier for the initial number of copies setting ({@value})*/ 
	public static final String NROF_COPIES = "nrofCopies";
	/** identifier for the binary-mode setting ({@value})*/ 
	public static final String BINARY_MODE = "binaryMode";
	/** SocialGeo router's settings name space ({@value})*/ 
	public static final String SOCIALGEO_NS = "SocialGeoRouter";
	/** Message property key */
	public static final String MSG_COUNT_PROPERTY = SOCIALGEO_NS + "." +
		"copies";
	
	protected int initialNrofCopies;
	protected boolean isBinary;
	protected HashMap<Long, Double> prMap; //<DTN host ID, probability>

	public SocialGeoRouter(Settings s) {
		super(s);
		Settings sgSettings = new Settings(SOCIALGEO_NS);
		
		//initialNrofCopies = sgSettings.getInt(NROF_COPIES);
		//isBinary = sgSettings.getBoolean( BINARY_MODE);
		
		prMap = new HashMap<Long, Double>();
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected SocialGeoRouter(SocialGeoRouter r) {
		super(r);
		//this.initialNrofCopies = r.initialNrofCopies;
		//this.isBinary = r.isBinary;
		
		this.prMap = new HashMap<Long, Double>();
	}
	
	/*
	@Override
	public void changedConnection(Connection con) {
		super.changedConnection(con);
		
		if (con.isUp()) {
			DTNHost otherHost = con.getOtherNode(getHost());
			System.out.println("connection changed");
		}
	}*/
	
	
	@Override
	public int receiveMessage(Message m, DTNHost from) {
		return super.receiveMessage(m, from);
	}
	/*
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message msg = super.messageTransferred(id, from);
		Integer nrofCopies = (Integer)msg.getProperty(MSG_COUNT_PROPERTY);
		
		assert nrofCopies != null : "Not a SG message: " + msg;
		
		if (isBinary) {
			nrofCopies = (int)Math.ceil(nrofCopies/2.0);
		}
		else {
			nrofCopies = 1;
		}
		
		msg.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
		return msg;
	}*/
	
	@Override 
	public boolean createNewMessage(Message msg) {
		makeRoomForNewMessage(msg.getSize());

		msg.setTtl(this.msgTtl);
		//msg.addProperty(MSG_COUNT_PROPERTY, new Integer(initialNrofCopies));
		addToMessages(msg, true);
		return true;
	}
	
	@Override
	public void update() {
		super.update();
		if (!canStartTransfer() || isTransferring()) {
			return; // nothing to transfer or is currently transferring 
		}

		/* try messages that could be delivered to final recipient */
		if (exchangeDeliverableMessages() != null) {
			return;
		}
		
		tryOtherMessages();	
	}
	
	
	private Tuple<Message, Connection> tryOtherMessages() {
		List<Tuple<Message, Connection>> messages = new ArrayList<Tuple<Message, Connection>>(); 
	
		Collection<Message> msgCollection = getMessageCollection();
		
		/* for all connected hosts collect all messages that have a higher
		   probability of delivery by the other host */
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			SocialGeoRouter othRouter = (SocialGeoRouter)other.getRouter();
			
			if (othRouter.isTransferring()) {
				continue; // skip hosts that are transferring
			}
			
			for (Message m : msgCollection) {
				if (othRouter.hasMessage(m.getId())) {
					continue; // skip messages that the other one has
				}
				if (othRouter.getPrFor(m.getTo()) >= 0.1 ||
						othRouter.getPrFor(m.getTo()) > getPrFor(m.getTo())) {
					// the other node has higher probability of delivery
					messages.add(new Tuple<Message, Connection>(m,con));
					//System.out.println(othRouter.getPrFor(m.getTo()) + " - " + getPrFor(m.getTo()));
				}
			}			
		}
		
		if (messages.size() == 0) {
			return null;
		}
		
		return tryMessagesForConnected(messages);	// try to send messages
	}
	
	
	private double getPrFor(DTNHost to) {
		double probability = 0;
		
		Path dstPath = to.getPath();
		Path myPath = getHost().getPath();
		
		try{
			probability = myPath.encounterProbability(dstPath);
		} catch(NullPointerException e) {
			return 0;
		}
		//System.out.println(getHost().getID() + " -- " + to.getID() + " : " + probability);
		
		return probability;
	}

	
	@Override
	public SocialGeoRouter replicate() {
		return new SocialGeoRouter(this);
	}
}
