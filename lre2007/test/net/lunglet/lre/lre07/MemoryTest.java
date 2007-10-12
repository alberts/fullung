package net.lunglet.lre.lre07;

import java.nio.ByteBuffer;

public class MemoryTest {
    public static void main(final String[] args) {
//        HANDLE HeapHandle = Kernel32.INSTANCE.GetProcessHeap();
//        boolean result = Kernel32.INSTANCE.HeapSetInformation(HeapHandle, 0, new IntByReference(2), new NativeLong(4));
//        System.out.println(result);
        int goodsize = 0;
        int oldsize = 0;
        int size = 1;
        while (true) {
            try {
                ByteBuffer.allocateDirect(size);
                System.out.println(size + " allocation succeeded");
                oldsize = size;
                goodsize = size;
                size *= 2;
            } catch (OutOfMemoryError e) {
                System.gc();
                System.out.println(size + " allocation failed, best so far = " + goodsize);
                size = oldsize + Math.abs(size - oldsize) / 2;
                if (Math.abs(size - oldsize) <= 1) {
                    System.out.println("last good size = " + goodsize);
                    break;
                }
            }
        }
    }
}
