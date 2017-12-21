import paramiko
import sys
import os
import time

machines = [{
    "order": 1,
    "class": "serverSide.Config.ServerConfig",
    "machine": "l040101-ws09.ua.pt"
},
    {
    "order": 2,
    "class": "serverSide.Logger.ServerLogger",
    "machine": "l040101-ws01.ua.pt"
},
    {
    "order": 3,
    "class": "serverSide.AssaultParties.ServerAssaultParty0",
    "machine": "l040101-ws04.ua.pt"
},
    {
    "order": 4,
    "class": "serverSide.AssaultParties.ServerAssaultParty1",
    "machine": "l040101-ws05.ua.pt"
},
    {
    "order": 5,
    "class": "serverSide.ControlCollectionSite.ServerControlCollection",
    "machine": "l040101-ws03.ua.pt"
},
    {
    "order": 6,
    "class": "serverSide.ConcentrationSite.ServerConcentrationSite",
    "machine": "l040101-ws07.ua.pt"
},
    {
    "order": 7,
    "class": "serverSide.Museum.ServerMuseum",
    "machine": "l040101-ws09.ua.pt"
},
    {
    "order": 8,
    "class": "clientSide.MasterThiefRun",
    "machine": "l040101-ws09.ua.pt"
},
    {
    "order": 9,
    "class": "clientSide.AssaultThiefRun",
    "machine": "l040101-ws09.ua.pt"
}]

COMMAND = 'java -cp %s %s'
USERNAME = "sd0202"
PASSWORD = "oquetuqueresseieu"


def sendFileToMachines():
    os.system("zip -r HeistToTheMuseumRMI.zip HeistToTheMuseumRMI")

    for s in machines:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        try:
            print ("Clearing directory")
            stdin, stdout, stderr = ssh.exec_command(
                "rm -rf HeistToTheMuseumRMI")
        except Exception:
            print ("Error clearing directory of the machine: " + s['machine'])
            break
        try:
            print ("Sending the zip to the machine: " + s['machine'])
            sftp = ssh.open_sftp()
            sftp.chdir("/home/sd0202/")
            sftp.put("HeistToTheMuseumRMI.zip",
                     "/home/sd0202/HeistToTheMuseumRMI.zip")
        except Exception, e:
            print ("Error sending zip to the machine: " +
                   s['machine'] + " Error: " + str(e))
            break
        try:
            print ("Unzipping the project in the machine: " + s['machine'])
            stdin, stdout, stderr = ssh.exec_command(
                "unzip HeistToTheMuseumRMI.zip")
            exit_status = stdout.channel.recv_exit_status()
            if exit_status != 0:
                print("Error", exit_status)
        except Exception, e:
            print ("Error unzipping project in the machine: " +
                   s['machine'] + " Error: " + str(e))
            break
        try:
            print ("Removing the zip file in machine: " + s['machine'])
            stdin, stdout, stderr = ssh.exec_command(
                "rm HeistToTheMuseumRMI.zip")
        except Exception:
            print ("Error Removing zip to the machine: " + s['machine'])
            break
        try:
            print ("Building the project in the machine: " +
                   s['machine'] + "\n")
            stdin, stdout, stderr = ssh.exec_command(
                "cd HeistToTheMuseumRMI; ant")
            exit_status = stdout.channel.recv_exit_status()
            if exit_status != 0:
                print("Error", exit_status)
        except Exception:
            print ("Error building the project in the machine: " +
                   s['machine'])
            break
        break
        try:
            print (
                "Moving the configurations file to dist directory in machine: " + s['machine'])
            stdin, stdout, stderr = ssh.exec_command(
                "cd HeistToTheMuseumRMI; mv Configurations.txt dist")
            exit_status = stdout.channel.recv_exit_status()
            if exit_status != 0:
                print("Error", exit_status)
        except Exception, e:
            print ("Error moving the file in the machine: " +
                   s['machine'] + " Error: " + str(e))
            break
        try:
            print ("Deploying the project in the machine: " +
                   s['machine'] + "\n")
            stdin, stdout, stderr = ssh.exec_command(
                "cd HeistToTheMuseumRMI/bin;cp interfaces/Register.class ../dir_registry/interfaces/\
                cp registry/*.class ../dir_registry/registry/\
                cp interfaces/*.class ../dir_serverSide/interfaces/\
                cp serverSide/*.class ../dir_serverSide/serverSide/\
                cp interfaces/LoggerInterface.class ../dir_clientSide/interfaces/\
                cp clientSide/*.class ../dir_clientSide/clientSide/\
                mkdir -p /home/sd0202/Public/classes\
                mkdir -p /home/sd0202/Public/classes/interfaces\
                mkdir -p /home/sd0202/Public/classes/clientSide\
                cp interfaces/*.class /home/sd0202/Public/classes/interfaces\
                cp clientSide/MasterThiefRun.class /home/sd0202/Public/classes/clientSide\
                cd ..\
                cp set_rmiregistry.sh /home/sd0202\
                cp set_rmiregistry_alt.sh /home/sd0202"
            )

            exit_status = stdout.channel.recv_exit_status()
            if exit_status != 0:
                print("Error", exit_status)
        except Exception:
            print ("Error deploying the project in the machine: " +
                   s['machine'])
            break
        ssh.close()


def runSimulation():
    for s in machines:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        classname = s['class'].split('.')[-1]
        # stdin,stdout,stderr = ssh.exec_command("pwd")
        # print (stdout.read())
        print ("Executing %s in %s" % (classname, s['machine']))
        print (COMMAND % (
            "HeistToTheMuseumRMI/dist/HeistToTheMuseumRMI.jar", s['class']))
        stdin, stdout, stderr = ssh.exec_command(COMMAND % (
            "HeistToTheMuseumRMI/dist/HeistToTheMuseumRMI.jar", s['class']))
        print ("Start : %s" % time.ctime())
        time.sleep(10)
        print ("End : %s" % time.ctime())
        ssh.close()


def killProcesses():
    for s in machines:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)

        ssh.exec_command("killall java")
        print ("Killing java processes in machine: " + s['machine'])
        ssh.close()


if __name__ == '__main__':
    ssh = paramiko.SSHClient()
    ssh.load_system_host_keys()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    sendFileToMachines()
    runSimulation()
    # killProcesses()

    print('End Operations!!!')
