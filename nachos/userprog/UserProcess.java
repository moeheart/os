package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;

import java.io.EOFException;

import java.util.LinkedList;

import java.io.IOException;


/**
 * Encapsulates the state of a user process that is not contained in its
 * user thread (or threads). This includes its address translation state, a
 * file table, and information about the program being executed.
 *
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 *
 * @see	nachos.vm.VMProcess
 * @see	nachos.network.NetProcess
 */
public class UserProcess {
	/**
	 * Allocate a new process.
	 */

	public int pid;
	public static int numProcess = 0;
	public static int numTotalProcess = 0;
	public boolean first;

	public UserProcess() {
//		        final String dir = System.getProperty("user.dir");
//				        System.out.println("current dir = " + dir);
		int numPhysPages = Machine.processor().getNumPhysPages();
		pageTable = new TranslationEntry[numPhysPages];
		//for (int i=0; i<numPhysPages; i++)
		//	pageTable[i] = new TranslationEntry(i,i, true,false,false,false);

		setFile(0,UserKernel.console.openForReading());
		setFile(1,UserKernel.console.openForWriting());
	}

	/**
	 * Allocate and return a new process of the correct class. The class name
	 * is specified by the <tt>nachos.conf</tt> key
	 * <tt>Kernel.processClassName</tt>.
	 *
	 * @return	a new process of the correct class.
	 */
	public static UserProcess newUserProcess() {
		return (UserProcess)Lib.constructObject(Machine.getProcessClassName());
	}

	/**
	 * Execute the specified program with the specified arguments. Attempts to
	 * load the program, and then forks a thread to run it.
	 *
	 * @param	name	the name of the file containing the executable.
	 * @param	args	the arguments to pass to the executable.
	 * @return	<tt>true</tt> if the program was successfully executed.
	 */
	public boolean execute(String name, String[] args) {
		//System.out.println(name);
		//System.out.println(args);
		if (!load(name, args))
			return false;

		numProcess++;
		numTotalProcess ++;
		first = (numTotalProcess == 1);
		pid = numTotalProcess;

		UThread thread =new UThread(this);

		threadToJoin = thread;
		
		thread.setName(name).fork();

		return true;
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		Machine.processor().setPageTable(pageTable);
	}

	/**
	 * Read a null-terminated string from this process's virtual memory. Read
	 * at most <tt>maxLength + 1</tt> bytes from the specified address, search
	 * for the null terminator, and convert it to a <tt>java.lang.String</tt>,
	 * without including the null terminator. If no null terminator is found,
	 * returns <tt>null</tt>.
	 *
	 * @param	vaddr	the starting virtual address of the null-terminated
	 *			string.
	 * @param	maxLength	the maximum number of characters in the string,
	 *				not including the null terminator.
	 * @return	the string read, or <tt>null</tt> if no null terminator was
	 *		found.
	 */
	public String readVirtualMemoryString(int vaddr, int maxLength) {
		Lib.assertTrue(maxLength >= 0);

		byte[] bytes = new byte[maxLength+1];

		int bytesRead = readVirtualMemory(vaddr, bytes);

		for (int length=0; length<bytesRead; length++) {
			if (bytes[length] == 0)
				return new String(bytes, 0, length);
		}

		return null;
	}

	/**
	 * Transfer data from this process's virtual memory to all of the specified
	 * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 *
	 * @param	vaddr	the first byte of virtual memory to read.
	 * @param	data	the array where the data will be stored.
	 * @return	the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data) {
		return readVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from this process's virtual memory to the specified array.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no
	 * data could be copied).
	 *
	 * @param	vaddr	the first byte of virtual memory to read.
	 * @param	data	the array where the data will be stored.
	 * @param	offset	the first byte to write in the array.
	 * @param	length	the number of bytes to transfer from virtual memory to
	 *			the array.
	 * @return	the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data, int offset,
			int length) {
		Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		// for now, just assume that virtual addresses equal physical addresses
		if (vaddr < 0 || vaddr >= memory.length)
			return 0;

		//int amount = Math.min(length, memory.length-vaddr);
		//System.arraycopy(memory, vaddr, data, offset, amount);

		int amount=0;
		for (int a=0;a<length && a+vaddr<memory.length;a++) {
			TranslationEntry page = pageTable[(a+vaddr)/pageSize];
			if (page == null || page.valid == false) {
				System.out.println("Cannot read");
				//return 0;
				break;
			}
			//page.used=true; nya
			data[a] = memory[page.ppn*pageSize+(a+vaddr)%pageSize];
			amount ++;
		}

		return amount;
	}

	/**
	 * Transfer all data from the specified array to this process's virtual
	 * memory.
	 * Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 *
	 * @param	vaddr	the first byte of virtual memory to write.
	 * @param	data	the array containing the data to transfer.
	 * @return	the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data) {
		return writeVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from the specified array to this process's virtual memory.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no
	 * data could be copied).
	 *
	 * @param	vaddr	the first byte of virtual memory to write.
	 * @param	data	the array containing the data to transfer.
	 * @param	offset	the first byte to transfer from the array.
	 * @param	length	the number of bytes to transfer from the array to
	 *			virtual memory.
	 * @return	the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data, int offset,
			int length) {
		Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		// for now, just assume that virtual addresses equal physical addresses
		if (vaddr < 0 || vaddr >= memory.length)
			return 0;

		//int amount = Math.min(length, memory.length-vaddr);
		//System.arraycopy(data, offset, memory, vaddr, amount);

		int amount=0;
		for (int a=0;a<length && a+vaddr<memory.length;a++) {
			TranslationEntry page = pageTable[(a+vaddr)/pageSize];
			//System.out.println(a);
			if (page == null || page.valid == false || page.readOnly == true) {
				System.out.println("Cannot write");
				//return -1;
				break;
			}
			memory[page.ppn*pageSize+(a+vaddr)%pageSize] = data[a];
			amount ++;
		}

		return amount;
	}

	/**
	 * Load the executable with the specified name into this process, and
	 * prepare to pass it the specified arguments. Opens the executable, reads
	 * its header information, and copies sections and arguments into this
	 * process's virtual memory.
	 *
	 * @param	name	the name of the file containing the executable.
	 * @param	args	the arguments to pass to the executable.
	 * @return	<tt>true</tt> if the executable was successfully loaded.
	 */
	private boolean load(String name, String[] args) {
		Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");

		if (UserKernel.fileManager.openFile(name) == false) return false;

		OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
		if (executable == null) {
			Lib.debug(dbgProcess, "\topen failed");
			return false;
		}

		try {
			coff = new Coff(executable);
		}
		catch (EOFException e) {
			executable.close();
			Lib.debug(dbgProcess, "\tcoff load failed");
			UserKernel.fileManager.closeFile(name);
			return false;
		}

		// make sure the sections are contiguous and start at page 0
		numPages = 0;
		for (int s=0; s<coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);
			if (section.getFirstVPN() != numPages) {
				coff.close();
				UserKernel.fileManager.closeFile(name);
				Lib.debug(dbgProcess, "\tfragmented executable");
				return false;
			}
			numPages += section.getLength();
		}

		// make sure the argv array will fit in one page
		byte[][] argv = new byte[args.length][];
		int argsSize = 0;
		for (int i=0; i<args.length; i++) {
			argv[i] = args[i].getBytes();
			// 4 bytes for argv[] pointer; then string plus one for null byte
			argsSize += 4 + argv[i].length + 1;
		}
		if (argsSize > pageSize) {
			coff.close();
			Lib.debug(dbgProcess, "\targuments too long");
			UserKernel.fileManager.closeFile(name);
			return false;
		}

		// program counter initially points at the program entry point
		initialPC = coff.getEntryPoint();	

		// next comes the stack; stack pointer initially points to top of it
		for (int a=0;a<stackPages;a++)
		{
			int vpn = a+numPages;
			int ppn = UserKernel.getFreePage();
			pageTable[vpn] = new TranslationEntry(vpn,ppn,true,false,false,false);
		}
		numPages += stackPages;
		initialSP = numPages*pageSize;


		// and finally reserve 1 page for arguments
		int ppn = UserKernel.getFreePage();
		pageTable[numPages] = new TranslationEntry(numPages,ppn,true,false,false,false);
		numPages++;

		if (!loadSections())
		{
			UserKernel.fileManager.closeFile(name);
			return false;
		}

		// store arguments in last page
		int entryOffset = (numPages-1)*pageSize;
		int stringOffset = entryOffset + args.length*4;

		this.argc = args.length;
		this.argv = entryOffset;

		for (int i=0; i<argv.length; i++) {
			byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
			Lib.assertTrue(writeVirtualMemory(entryOffset,stringOffsetBytes) == 4);
			entryOffset += 4;
			Lib.assertTrue(writeVirtualMemory(stringOffset, argv[i]) ==
					argv[i].length);
			stringOffset += argv[i].length;
			Lib.assertTrue(writeVirtualMemory(stringOffset,new byte[] { 0 }) == 1);
			stringOffset += 1;
		}

		return true;
	}

	/**
	 * Allocates memory for this process, and loads the COFF sections into
	 * memory. If this returns successfully, the process will definitely be
	 * run (this is the last step in process initialization that can fail).
	 *
	 * @return	<tt>true</tt> if the sections were successfully loaded.
	 */
	protected boolean loadSections() {
		if (numPages > Machine.processor().getNumPhysPages()) {
			coff.close();
			Lib.debug(dbgProcess, "\tinsufficient physical memory");
			return false;
		}

		// load sections
		for (int s=0; s<coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);

			Lib.debug(dbgProcess, "\tinitializing " + section.getName()
					+ " section (" + section.getLength() + " pages)");

			for (int i=0; i<section.getLength(); i++) {
				int vpn = section.getFirstVPN()+i;
				int ppn = UserKernel.getFreePage();

				pageTable[vpn] = new TranslationEntry(vpn,ppn,true,section.isReadOnly(),false,false);

				// for now, just assume virtual addresses=physical addresses
				//section.loadPage(i, vpn);
				section.loadPage(i, ppn);
			}
		}

		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		for (int a=0;a<numPages;a++)
		{
			//System.out.println(pageTable[a]);
			if (pageTable[a]!=null) UserKernel.addFreePage(pageTable[a].ppn);
		}
		for (int a=0;a<maxFileNumber;a++)
		{
			OpenFile file = getFile(a);
			if (file != null) 
			{
				file.close();
				closeFile(a);
			}
		}
	}    

	/**
	 * Initialize the processor's registers in preparation for running the
	 * program loaded into this process. Set the PC register to point at the
	 * start function, set the stack pointer register to point at the top of
	 * the stack, set the A0 and A1 registers to argc and argv, respectively,
	 * and initialize all other registers to 0.
	 */
	public void initRegisters() {
		Processor processor = Machine.processor();

		// by default, everything's 0
		for (int i=0; i<processor.numUserRegisters; i++)
			processor.writeRegister(i, 0);

		// initialize PC and SP according
		processor.writeRegister(Processor.regPC, initialPC);
		processor.writeRegister(Processor.regSP, initialSP);

		// initialize the first two argument registers to argc and argv
		processor.writeRegister(Processor.regA0, argc);
		processor.writeRegister(Processor.regA1, argv);
	}

	class zhxException extends Exception {
		public zhxException(String msg) {
			super(msg);
		}
	}

	void checkAndThrow(boolean expr,String msg) throws zhxException {
		if (expr) throw new zhxException(msg);
	}

	void checkAddress(int add) throws zhxException {
		checkAndThrow(add<0 || add>=numPages*pageSize,"Error Address");
	}

	/**
	 * Handle the halt() system call. 
	 */
	private int handleHalt() {
		if (first == false) return -1;

		Machine.halt();

		Lib.assertNotReached("Machine.halt() did not halt machine!");
		return 0;
	}

	private int handleExit(int status) {
		retCode = status;
		unloadSections();
		numProcess--;
		//System.out.println("ggwp"+numProcess);
		if (numProcess == 0) Kernel.kernel.terminate();
		else UThread.finish();

		return 0;
	}

	LinkedList<UserProcess> processList = new LinkedList<>();
	
	private int handleExec(int fileNameAdd,int argc,int argvAdd) {
		/*try{
		java.lang.Runtime rt = Runtime.getRuntime();
		java.lang.Process pr = rt.exec("pwd");
		}
		catch (IOException e)
		{
		}
		finally{
		}*/
		int status = -1;

		try {
			checkAddress(fileNameAdd);
			String filename = readVirtualMemoryString(fileNameAdd,256);
			System.out.println(filename);
			checkAndThrow(argc<0,"argc less than 0");

			checkAddress(argvAdd);
			String[] argv = new String[argc];
			for (int a=0;a<argc;a++)
			{
				byte[] nowop = new byte[4];
				int numRead = readVirtualMemory(argvAdd+4*a,nowop,0,4);
				checkAndThrow(numRead != 4,"Something wrong in read");
				int nowAdd = Lib.bytesToInt(nowop,0,4);

				checkAddress(nowAdd);

				argv[a] = readVirtualMemoryString(nowAdd,256);
			}
			UserProcess process = new UserProcess();
			if (process.execute(filename,argv)) status = process.pid;
			else status = -1;
			processList.add(process);
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	boolean work = true;
	int retCode;
	UThread threadToJoin = null;
	boolean joinOnlyOnce = false;

	private int handleJoin(int pid,int statusAdd) {
		int status = -1;
		
		try{
			checkAddress(statusAdd);

			boolean find = false;

			for (int a=0;a<processList.size();a++)
				if (processList.get(a).pid == pid)
				{
					find=true;
					UserProcess process = processList.get(a);
					//System.out.println(process);
					//System.out.println(process.threadToJoin);
					checkAndThrow(process.joinOnlyOnce,"Can only join once");
					process.joinOnlyOnce = true;
					process.threadToJoin.join();
					if (process.work) status=1;
					else status=0;
					int code = process.retCode;
					byte[] codeByte = Lib.bytesFromInt(code);
					int numWrite = writeVirtualMemory(statusAdd,codeByte,0,4);
					checkAndThrow(numWrite !=4,"Something error while writing");
					break;
				}
			checkAndThrow(!find,"No process to join");
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	public final int maxFileNumber = 16;
	
	OpenFile[] fileList = new OpenFile[16];

	public int getFD() {
		for (int a=0;a<fileList.length;a++)
			if (fileList[a] == null) return a;
		return -1;
	}

	public OpenFile getFile(int fd) {
		return fileList[fd];
	}

	public void closeFile(int fd) {
		fileList[fd] = null;
	}

	public void setFile(int fd,OpenFile file) {
		fileList[fd] = file;
	}


	private int handleCreate(int fileNameAdd) {
		int status = -1;
		//System.out.println("creating something");

		try {
			checkAddress(fileNameAdd);
			String fileName = readVirtualMemoryString(fileNameAdd,256);
			int fd = getFD();
			checkAndThrow(fd == -1, "File full");
			checkAndThrow(UserKernel.fileManager.openFile(fileName) == false,"Something wrong while openning");
			OpenFile file = UserKernel.fileSystem.open(fileName,true);
			checkAndThrow(file == null, "Cannot open file");
			setFile(fd,file);
			status=fd;
		}
		catch (zhxException e)
		{
			e.printStackTrace();
		}

		return status;
	}

	private int handleOpen(int fileNameAdd) {
		int status = -1;

		try {
			checkAddress(fileNameAdd);
			String fileName = readVirtualMemoryString(fileNameAdd,256);
			int fd = getFD();
			checkAndThrow(fd == -1, "File full");
			checkAndThrow(UserKernel.fileManager.openFile(fileName) == false,"Something wrong while openning");
			OpenFile file = UserKernel.fileSystem.open(fileName,false);
			//System.out.println(file);
			checkAndThrow(file == null,"Cannot open file");
			setFile(fd,file);
			status = fd;
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	private int handleRead(int fd,int bufferAdd,int size) {
		int status = -1;

		try {
			checkAndThrow(size < 0,"Error reading size");
			checkAddress(bufferAdd);
			checkAddress(bufferAdd+size-1);
			checkAndThrow(fd<0 || fd>=maxFileNumber,"fd out of range");
			OpenFile file = getFile(fd);
			checkAndThrow(file == null,"No such file");
			
			byte[] buffer = new byte[size];
			int numRead = file.read(buffer,0,size);	
			checkAndThrow(numRead == -1,"Something wrong while reading file");
			int numWrite = writeVirtualMemory(bufferAdd,buffer,0,numRead);
			checkAndThrow(numRead != numWrite,"Something wrong while writing to memory");
			
			status = numRead;
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	private int handleWrite(int fd,int bufferAdd,int size) {
		int status = -1;

		try {
			checkAndThrow(size < 0,"Error reading size");
			checkAddress(bufferAdd);
			checkAddress(bufferAdd+size-1);
			checkAndThrow(fd<0 || fd>=maxFileNumber,"fd out of range");

			OpenFile file = getFile(fd);
			checkAndThrow(file == null,"No such file");

			byte[] buffer = new byte[size];
			int numRead = readVirtualMemory(bufferAdd,buffer,0,size);
			int numWrite = file.write(buffer,0,size);
			checkAndThrow(numRead != numWrite,"Something wrong while writing to the file");

			status = numRead;
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	private int handleClose(int fd) {
		int status = -1;
		
		try {
			checkAndThrow(fd<0 || fd>=maxFileNumber,"fd out of range");

			OpenFile file = getFile(fd);
			checkAndThrow(file == null,"No such file");
			String fileName = file.getName();
			UserKernel.fileManager.closeFile(fileName);

			file.close();
			closeFile(fd);

			status = 0;
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}

	private int handleUnlink(int fileNameAdd) {
		int status = -1;

		try {
			checkAddress(fileNameAdd);
			String fileName = readVirtualMemoryString(fileNameAdd,256);
			checkAndThrow(UserKernel.fileManager.removeFile(fileName) == false,"Something wrong while removing");
			boolean success = UserKernel.fileSystem.remove(fileName);
			checkAndThrow(success == false,"Remove failed");

			status = 0;
		}
		catch (zhxException e) {
			e.printStackTrace();
		}

		return status;
	}


	private static final int
		syscallHalt = 0,
					syscallExit = 1,
					syscallExec = 2,
					syscallJoin = 3,
					syscallCreate = 4,
					syscallOpen = 5,
					syscallRead = 6,
					syscallWrite = 7,
					syscallClose = 8,
					syscallUnlink = 9;

	/**
	 * Handle a syscall exception. Called by <tt>handleException()</tt>. The
	 * <i>syscall</i> argument identifies which syscall the user executed:
	 *
	 * <table>
	 * <tr><td>syscall#</td><td>syscall prototype</td></tr>
	 * <tr><td>0</td><td><tt>void halt();</tt></td></tr>
	 * <tr><td>1</td><td><tt>void exit(int status);</tt></td></tr>
	 * <tr><td>2</td><td><tt>int  exec(char *name, int argc, char **argv);
	 * 								</tt></td></tr>
	 * <tr><td>3</td><td><tt>int  join(int pid, int *status);</tt></td></tr>
	 * <tr><td>4</td><td><tt>int  creat(char *name);</tt></td></tr>
	 * <tr><td>5</td><td><tt>int  open(char *name);</tt></td></tr>
	 * <tr><td>6</td><td><tt>int  read(int fd, char *buffer, int size);
	 *								</tt></td></tr>
	 * <tr><td>7</td><td><tt>int  write(int fd, char *buffer, int size);
	 *								</tt></td></tr>
	 * <tr><td>8</td><td><tt>int  close(int fd);</tt></td></tr>
	 * <tr><td>9</td><td><tt>int  unlink(char *name);</tt></td></tr>
	 * </table>
	 * 
	 * @param	syscall	the syscall number.
	 * @param	a0	the first syscall argument.
	 * @param	a1	the second syscall argument.
	 * @param	a2	the third syscall argument.
	 * @param	a3	the fourth syscall argument.
	 * @return	the value to be returned to the user.
	 */
	public int handleSyscall(int syscall, int a0, int a1, int a2, int a3) {
		//if (syscall != 6 && syscall != 7) System.out.println(syscall + " " + a0 + " " + a1 + " " + a2 + " " + a3);
		switch (syscall) {
			case syscallHalt:
				return handleHalt();
			case syscallExit:
				return handleExit(a0);
			case syscallExec:
				return handleExec(a0,a1,a2);
			case syscallJoin:
				return handleJoin(a0,a1);
			case syscallCreate:
				return handleCreate(a0);
			case syscallOpen:
				return handleOpen(a0);
			case syscallRead:
				return handleRead(a0,a1,a2);
			case syscallWrite:
				return handleWrite(a0,a1,a2);
			case syscallClose:
				return handleClose(a0);
			case syscallUnlink:
				return handleUnlink(a0);


			default:
				Lib.debug(dbgProcess, "Unknown syscall " + syscall);
				Lib.assertNotReached("Unknown system call!");
				work = false;
		}
		return 0;
	}

	/**
	 * Handle a user exception. Called by
	 * <tt>UserKernel.exceptionHandler()</tt>. The
	 * <i>cause</i> argument identifies which exception occurred; see the
	 * <tt>Processor.exceptionZZZ</tt> constants.
	 *
	 * @param	cause	the user exception that occurred.
	 */
	public void handleException(int cause) {
		Processor processor = Machine.processor();

		switch (cause) {
			case Processor.exceptionSyscall:
				int result = handleSyscall(processor.readRegister(Processor.regV0),
						processor.readRegister(Processor.regA0),
						processor.readRegister(Processor.regA1),
						processor.readRegister(Processor.regA2),
						processor.readRegister(Processor.regA3)
						);
				processor.writeRegister(Processor.regV0, result);
				processor.advancePC();
				break;				       

			default:
				System.out.println(cause);
				Lib.debug(dbgProcess, "Unexpected exception: " +
						Processor.exceptionNames[cause]);
				work = false;
				handleExit(cause);
				Lib.assertNotReached("Unexpected exception");
		}
	}

	/** The program being run by this process. */
	protected Coff coff;

	/** This process's page table. */
	protected TranslationEntry[] pageTable;
	/** The number of contiguous pages occupied by the program. */
	protected int numPages;

	/** The number of pages in the program's stack. */
	protected final int stackPages = 8;

	private int initialPC, initialSP;
	private int argc, argv;

	private static final int pageSize = Processor.pageSize;
	private static final char dbgProcess = 'a';

}
