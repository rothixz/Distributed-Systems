ant
cd bin/
cp interfaces/Register.class ../dir_registry/interfaces/
cp registry/*.class ../dir_registry/registry/
cp interfaces/*.class ../dir_serverSide/interfaces/
cp serverSide/*.class ../dir_serverSide/serverSide/
cp interfaces/LoggerInterface.class ../dir_clientSide/interfaces/
cp clientSide/*.class ../dir_clientSide/clientSide/
mkdir -p /home/mota/Public/classes
mkdir -p /home/mota/Public/classes/interfaces
mkdir -p /home/mota/Public/classes/clientSide
cp interfaces/*.class /home/mota/Public/classes/interfaces
cp clientSide/MasterThiefRun.class /home/mota/Public/classes/clientSide
cd ..
cp set_rmiregistry.sh /home/mota
cp set_rmiregistry_alt.sh /home/mota