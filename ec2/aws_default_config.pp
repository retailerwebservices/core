$default_path = "/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin:/opt/aws/bin:/home/ec2-user/.local/bin:/home/ec2-user/bin"

class files
{
file
{

'/home/ec2-user/downloads' : 
        ensure => 'directory',
        owner => 'ec2-user'

}

file
{

'/home/ec2-user/bin' :
        ensure => 'directory',
        owner => 'ec2-user'

}

file
{
'/home/ec2-user/src' :
        ensure => 'directory',
        owner => 'ec2-user',
} 

}


class java
{

package
{

'java-1.8.0-openjdk-devel.x86_64' : 
	ensure => present, 
        allow_virtual => false 
} 

 
package 
{
 
'java-1.7.0-openjdk.x86_64' : 
	ensure => purged,
        allow_virtual => false

}


}

class git
{

package
{
'git' : 
	ensure => present,
	allow_virtual => false,
}

}

class clone_repos
{
exec
{

'git clone https://github.com/jim-kane/jimmutable.git' :
	cwd => '/home/ec2-user/src',
	creates => '/home/ec2-user/src/jimmutable',
	path => $default_path,
	user => 'ec2-user',
}
}

class maven
{

exec
{

'wget -O mvn.tar.gz http://mirrors.ibiblio.org/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz' :
	cwd => '/home/ec2-user/downloads',
	creates => '/home/ec2-user/downloads/mvn.tar.gz',
	path => $default_path
}

exec
{

'tar -xvf mvn.tar.gz -C ../bin' :
	cwd => '/home/ec2-user/downloads',
	creates => '/home/ec2-user/bin/apache-maven-3.3.9',
	path => $default_path

}

file 
{

'/etc/profile.d/maven.sh' :
	ensure => 'present',
	content => "export PATH=\$PATH:/home/ec2-user/bin/apache-maven-3.3.9/bin\nexport M2_HOME=/home/ec2-user/bin/apache-maven-3.3.9\nexport M2=/home/ec2-user/bin/apache-maven-3.3.9/bin\nexport MAVEN_OPTS=-Xmx1024m\n"

}

}

node default
{
	include files
	include java
	include git
	include clone_repos
	include maven
}
