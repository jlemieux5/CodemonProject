All: 
		javac Codemon.java codemonTrainer.java fightClub.java mainMenu.java trainingCenter.java aboutMenu.java  helpMenu.java 
		javah -jni Codemon
		lex parse.l 
		yacc -d parse.y
		gcc -dynamiclib -o libcodemon.jnilib -I/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/include -I/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/include/darwin codemon.c client.c lex.yy.c y.tab.c -ll -framework JavaVM
