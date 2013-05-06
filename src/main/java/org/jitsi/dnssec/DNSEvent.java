/*
 * $Id: DNSEvent.java 286 2005-12-03 01:07:16Z davidb $
 *
 * Copyright (c) 2005 VeriSign, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.jitsi.dnssec;

import java.util.*;

import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

/**
 * This is the core event class. A DNSEvent represents either a request or a
 * request, response pair. Note that a request may be modified by the resolution
 * process, so this class keeps track of the original request.
 * 
 * DNSEvents are frequently created in response to the needs of another event.
 * The new event is chained to the old event forming a dependency chain.
 * 
 * @author davidb
 * @version $Revision: 286 $
 */
public class DNSEvent implements Cloneable {

    /**
     * This is the current, mutable request -- this request will change based on
     * the current needs of a module.
     */
    private Message currentRequest;
    /**
     * This is the original, immutable request. This request must not be changed
     * after being set.
     */
    private Message originalRequest;

    /**
     * This is the normal response to the current request. It may be modified as
     * it travels through the chain, but only the most recent is relevant.
     */
    private SMessage response;

    /**
     * If event was created on behalf of another event, the "next" event is that
     * original event. That is, if this is not null, some other event is
     * (possibly) waiting on the completion of this one.
     */
    private DNSEvent mForEvent;

    /**
     * This is a map of per-module state. Each resolver module has the
     * opportunity to create a custom per-event state object that it can attach
     * to this event. The keys for this map are references to the module itself,
     * typically.
     */
    private Map<Resolver, Object> mModuleStateMap;

    /**
     * This is the dependency depth of this event -- in other words, the length
     * of the "nextEvent" chain.
     */
    private int depth;

    /**
     * Create a empty event. This is typically only used internally.
     */
    protected DNSEvent() {
        mModuleStateMap = new HashMap<Resolver, Object>();
    }

    /**
     * Create a request event.
     * 
     * @param request The initial request.
     */
    public DNSEvent(Message request) {
        this();
        originalRequest = request;
        currentRequest = (Message)request.clone();
    }

    /**
     * Create a local, dependent event.
     * 
     * @param request The initial request.
     * @param forEvent The dependent event.
     */
    public DNSEvent(Message request, DNSEvent forEvent) {
        this(request);

        mForEvent = forEvent;
        depth = forEvent.getDepth() + 1;
    }

    /**
     * @return The current request.
     */
    public Message getRequest() {
        return currentRequest;
    }

    /**
     * @return The current request.
     */
    public void setRequest(Message request) {
        currentRequest = request;
    }

    /**
     * @return The original request. Do not modify this!
     */
    public Message getOrigRequest() {
        return originalRequest;
    }

    /**
     * @return The "for" event. I.e., the event that is depending on this event.
     */
    public DNSEvent forEvent() {
        return mForEvent;
    }

    /**
     * @return The response that has been attached to this event, or null if one
     *         hasn't been attached yet.
     */
    public SMessage getResponse() {
        return response;
    }

    /**
     * Attach a response to this event.
     * 
     * @param response The response message to attach. The must match the
     *            current request at time of attachment.
     */
    public void setResponse(SMessage response) {
        this.response = response;
    }

    /**
     * Fetch any attached per-module state for this event.
     * 
     * @param module A reference for the module itself. This is the key.
     * @return A state object for the module, or null if one wasn't attached.
     */
    public Object getModuleState(Resolver module) {
        return mModuleStateMap.get(module);
    }

    /**
     * Attach per-module state to this event.
     * 
     * @param module A reference for the module itself. This is the key.
     * @param state A state object.
     */
    public void setModuleState(Resolver module, Object state) {
        mModuleStateMap.put(module, state);
    }

    /**
     * @return The depth of this event. The depth is the events position in a
     *         dependency chain of events.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Clone the event.
     */
    public Object clone() {
        try {
            DNSEvent event = (DNSEvent) super.clone();

            return event;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * @return A string representation of the event, to be used in logging,
     *         perhaps.
     */
    public String toString() {
        return super.toString() + " " + currentRequest.toString();
    }
}