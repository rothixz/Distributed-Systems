java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerLogger &
sleep 1
java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerMuseum &
sleep 1
java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerConcentrationSite &
sleep 1
java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerControlCollectionSite &
sleep 1
java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerAssaultParty0 &
sleep 1
java -Djava.rmi.server.codebase="file:///home/mota/Desktop/Back_Engine_Alunos/dir_serverSide/"\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.ServerAssaultParty1