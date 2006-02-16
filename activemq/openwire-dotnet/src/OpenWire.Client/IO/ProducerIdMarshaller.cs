//
// Marshalling code for Open Wire Format for ProducerId
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Client;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using OpenWire.Client.IO;

namespace OpenWire.Client.IO
{
    public class ProducerIdMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new ProducerId();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            ProducerId info = (ProducerId) command;
            info.ConnectionId = dataIn.ReadString();
            info.Value = dataIn.ReadInt64();
            info.SessionId = dataIn.ReadInt64();

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            ProducerId info = (ProducerId) command;
            dataOut.Write(info.ConnectionId);
            dataOut.Write(info.Value);
            dataOut.Write(info.SessionId);

        }
    }
}
