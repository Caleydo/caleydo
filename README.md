Caleydo - Visualization for Molecular Biology
=============================================

Caleydo is a visualization framework for molecular biology data. It is targeted at analyzing multiple heterogeneous but related tabular datasets (e.g.,  mRNA expression, copy number status and clinical variables), stratifications or clusters in these datasets and their relationships to biological pathways.  

For user documentation and installation based on binaries please refer to the [Caleydo Website](http://caleydo.org). This guide assumes that you want to install Caleydo from source. 

Installation
============

Caleydo uses Java, OpenGL and the Eclipse Rich Client Platform (RCP). Things you need to install before being able to run Caleydo: 

 * Eclipse Juno for RCP and RAP Developers, which you can get from the [eclipse download page](http://www.eclipse.org/downloads/). *Other Eclipse versions won't work*. 
 * Install [EGit](http://www.eclipse.org/egit/download/) in Eclipse using software updates.
 * Java SDK >= 1.7

To install Caleydo use EGit within Eclipse and clone the repository. Each directory in the caleydo-dev folder corresponds to an Eclipse project. [Here](http://www.vogella.com/articles/EGit/article.html) is a good tutorial on how to import Eclipse projects from git.  

If you want to use ssh (instead of https) for communicating with github out of eclipse follow [these](http://wiki.eclipse.org/EGit/User_Guide#Eclipse_SSH_Configuration) instructions.
You will have to generate a new RSA key and save it to you ~/.ssh folder. Remeber to set a passphrase for you key. This will result in a file ida_rsa and ida_rsa.pub turning up in your ssh folder.
Save your public rsa key with your eclipse account folder.

When cloning the repository follow the above tutorial. Don't change the username "git" to your username!
 
Team
====

Caleydo is an academic project currently developed by members of

 * [Institute for Computer Graphics and Vision](http://www.icg.tugraz.at/) at Graz University of Technology, Austria
 * [Institute of Computer Graphics](http://www.jku.at/cg/) at Johannes Kepler University Linz, Austria
 * [Pfister Lab](http://gvi.seas.harvard.edu/pfister) at the School of Engineering and Applied Sciences, Harvard University, Cambridge, USA
 * [Park Lab](http://compbio.med.harvard.edu/) at Harvard Medical School, Boston, USA

