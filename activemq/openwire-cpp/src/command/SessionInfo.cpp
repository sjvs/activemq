/*
 * Copyright 2006 The Apache Software Foundation or its licensors, as
 * applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "command/SessionInfo.hpp"

using namespace apache::activemq::client::command;

/*
 * Dummy, should be auto-generated
 */
SessionInfo::SessionInfo()
{
}

SessionInfo::~SessionInfo()
{
}

p<SessionId> SessionInfo::getSessionId()
{
    return sessionId ;
}

void SessionInfo::setSessionId(p<SessionId> sessionId)
{
    this->sessionId = sessionId ;
}
