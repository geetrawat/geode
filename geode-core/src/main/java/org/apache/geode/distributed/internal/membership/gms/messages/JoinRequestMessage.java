/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.distributed.internal.membership.gms.messages;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.geode.DataSerializer;
import org.apache.geode.distributed.internal.membership.gms.GMSMember;
import org.apache.geode.distributed.internal.membership.gms.GMSUtil;
import org.apache.geode.internal.Version;

public class JoinRequestMessage extends AbstractGMSMessage {
  private GMSMember memberID;
  private Object credentials;
  private int failureDetectionPort = -1;
  private int requestId;
  private boolean useMulticast;

  public JoinRequestMessage(GMSMember coord, GMSMember id,
      Object credentials, int fdPort, int requestId) {
    super();
    if (coord != null) {
      setRecipient(coord);
    }
    this.memberID = id;
    this.credentials = credentials;
    this.failureDetectionPort = fdPort;
    this.requestId = requestId;
  }

  public JoinRequestMessage() {
    // no-arg constructor for serialization
  }

  public int getRequestId() {
    return requestId;
  }

  @Override
  public int getDSFID() {
    return JOIN_REQUEST;
  }

  @Override
  public boolean getMulticast() {
    return useMulticast;
  }

  @Override
  public void setMulticast(boolean useMulticast) {
    this.useMulticast = useMulticast;
  }

  public GMSMember getMemberID() {
    return memberID;
  }

  public Object getCredentials() {
    return credentials;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + memberID
        + (credentials == null ? ")" : "; with credentials)") + " failureDetectionPort:"
        + failureDetectionPort;
  }

  @Override
  public Version[] getSerializationVersions() {
    return null;
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    GMSUtil.writeMemberID(memberID, out);
    DataSerializer.writeObject(credentials, out);
    DataSerializer.writePrimitiveInt(failureDetectionPort, out);
    // preserve the multicast setting so the receiver can tell
    // if this is a mcast join request
    out.writeBoolean(false);
    out.writeInt(requestId);
  }

  @Override
  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    memberID = GMSUtil.readMemberID(in);
    credentials = DataSerializer.readObject(in);
    failureDetectionPort = DataSerializer.readPrimitiveInt(in);
    // setMulticast(in.readBoolean());
    in.readBoolean();
    requestId = in.readInt();
  }

  public int getFailureDetectionPort() {
    return failureDetectionPort;
  }

  @Override
  public int hashCode() {
    return Objects.hash(memberID);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JoinRequestMessage other = (JoinRequestMessage) obj;
    if (credentials == null) {
      if (other.credentials != null)
        return false;
    } else if (!credentials.equals(other.credentials))
      return false;
    if (failureDetectionPort != other.failureDetectionPort)
      return false;
    if (memberID == null) {
      if (other.memberID != null)
        return false;
    } else if (!memberID.equals(other.memberID))
      return false;
    if (requestId != other.requestId)
      return false;
    return true;
  }
}
