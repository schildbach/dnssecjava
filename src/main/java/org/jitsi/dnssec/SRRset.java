/*
 * $Id: SRRset.java 286 2005-12-03 01:07:16Z davidb $
 * 
 * Copyright (c) 2005 VeriSign. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package org.jitsi.dnssec;

import java.util.*;

import org.xbill.DNS.*;

/**
 * A version of the RRset class overrides the standard security status.
 * 
 * @author davidb
 * @version $Revision: 286 $
 */
public class SRRset extends RRset {
    private SecurityStatus securityStatus;

    /** Create a new, blank SRRset. */
    public SRRset() {
        super();
        securityStatus = SecurityStatus.UNCHECKED;
    }

    /**
     * Create a new SRRset from an existing RRset. This SRRset will contain that
     * same internal Record objects as the original RRset.
     */
    public SRRset(RRset r) {
        this();

        for (Iterator<?> i = r.rrs(); i.hasNext();) {
            addRR((Record) i.next());
        }

        for (Iterator<?> i = r.sigs(); i.hasNext();) {
            addRR((Record) i.next());
        }
    }

    /**
     * Return the current security status (generally: UNCHECKED, BOGUS, or
     * SECURE).
     */
    public SecurityStatus getSecurityStatus() {
        return securityStatus;
    }

    /**
     * Set the current security status for this SRRset. This status will be
     * shared amongst all copies of this SRRset (created with cloneSRRset())
     */
    public void setSecurityStatus(SecurityStatus status) {
        securityStatus = status;
    }

    /**
     * @return The "signer" name for this SRRset, if signed, or null if not.
     */
    public Name getSignerName() {
        Iterator<?> sigs = sigs();
        if (sigs.hasNext()) {
            return ((RRSIGRecord) sigs.next()).getSigner();
        }

        return null;
    }
}