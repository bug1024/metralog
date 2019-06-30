/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dianping.cat.message.io;

public class ClientMessage {


	public final static int PROTOCOL_ID = 1667331171;

	public final static int VERSION_0 = 0;

	private final int m_protocolId;

	private final int m_version;

	private final byte[] m_data;

	public ClientMessage(int protocolId, int version, byte[] data) {
		m_protocolId = protocolId;
		m_version = version;
		m_data = data;
	}

	public ClientMessage(int protocolId, int version, String data) {
		m_protocolId = protocolId;
		m_version = version;
		m_data = data.getBytes();
	}

	public byte[] getData() {
		return m_data;
	}

	public int getProtocolId() {
		return m_protocolId;
	}

	public int getVersion() {
		return m_version;
	}

}
