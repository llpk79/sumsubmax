import java.util.*;

public class Main {
    public static void main(String[] args) {
        int[] array = {1, 3, 2};
        int[] left = {0, 0, 0, 1, 1, 2};
        int[] right = {0, 1, 2, 1, 2, 2};
        SegmentMax solver = new SegmentMax();
        solver.solve(array, left, right);
    }

    static class SegmentMax {
        int arrayLen;
        int num_queries;
        int[] left;
        int[] right;
        int[] nextGTPositionLeft;
        int[] nextGTEPositionRight;
        final List<Event> events = new ArrayList<>();

        public void solve(int[] array, int[] left, int[] right) {
            this.right = right;
            this.left = left;
            arrayLen = array.length;
            num_queries = left.length;
            nextGTPositionLeft = new int[arrayLen];
            nextGTEPositionRight = new int[arrayLen];
            final Stack<Integer> stack = new Stack<>() ;
            for (int i = 0; i < arrayLen; i++) {
                if (stack.empty()) {
                    stack.push(i);
                } else if (array[i] <= array[stack.peek()]) {
                    stack.push(i);
                } else {
                    while (!stack.empty() && array[i] > array[stack.peek()]) {
                        int index = stack.pop();
                        if (!stack.empty()) {
                            this.nextGTPositionLeft[index] = index - (index - stack.peek());
                            this.nextGTEPositionRight[index] = arrayLen + (i - arrayLen);
                        } else {
                            this.nextGTPositionLeft[index] = index - (index + 1);
                            this.nextGTEPositionRight[index] = arrayLen + (i - arrayLen);
                        }
                    }
                    stack.push(i);
                }
            }
            while (!stack.empty()) {
                int index = stack.pop();
                if (!stack.empty()) {
                    this.nextGTPositionLeft[index] = index - (index - stack.peek());
                    this.nextGTEPositionRight[index] = arrayLen;
                }
                else {
                    this.nextGTPositionLeft[index] = -1;
                    this.nextGTEPositionRight[index] = arrayLen;
                }
            }
            for(int i = 0; i < arrayLen; i++) {
                int nextGTToLeft = nextGTPositionLeft[i] + 1, nextGTEToRight = nextGTEPositionRight[i] - 1;

                addQuad(0, nextGTToLeft - 1, nextGTEToRight + 1, arrayLen - 1,
                        0, 0, 0, (long) (i - nextGTToLeft + 1) * (nextGTEToRight - i + 1) * array[i]);

                addQuad(nextGTToLeft, i, nextGTEToRight + 1, arrayLen - 1,
                        0, (long) (i - nextGTEToRight - 1) * array[i], 0, (long) (i + 1) * (nextGTEToRight - i + 1) * array[i]);

                addQuad(0, nextGTToLeft - 1, i, nextGTEToRight,
                        0, 0, (long) (i - nextGTToLeft + 1) * array[i], (long) (1 - i) * (i - nextGTToLeft + 1) * array[i]);

                addQuad(nextGTToLeft, i, i, nextGTEToRight,
                        -array[i], (long) (i - 1) * array[i], (long) (i + 1) * array[i], ((-1L * i * i) + 1) * array[i]);
            }
            for (int i = 0; i < num_queries; i++) {
                events.add(Event.point(left[i], i));
            }
            Collections.sort(events);
            final Fenwick LR = new Fenwick(arrayLen), L = new Fenwick(arrayLen), R = new Fenwick(arrayLen), C = new Fenwick(arrayLen);
            final long[] ansLR = new long[num_queries], ansL = new long[num_queries], ansR = new long[num_queries], ansC = new long[num_queries];
            for (Event event : events) {
                if (event.type == -1 || event.type == 1) {
                    LR.update(event.l, event.r, event.LR * -event.type);
                    L.update(event.l, event.r, event.L * -event.type);
                    R.update(event.l, event.r, event.R * -event.type);
                    C.update(event.l, event.r, event.C * -event.type);
                }
                if (event.type == 0) {
                    int r = this.right[event.index];
                    ansLR[event.index] = LR.getValue(r);
                    ansL[event.index] = L.getValue(r);
                    ansR[event.index] = R.getValue(r);
                    ansC[event.index] = C.getValue(r);
                }
            }
            for (int i = 0; i < num_queries; i++) {
                System.out.println(ansLR[i] * left[i] * right[i] + ansL[i] * left[i] + ansR[i] * right[i] + ansC[i]);
            }
        }

        void addQuad(int l, int r, int b, int t, long LR, long L, long R, long C) {
            if (l > r || b > t) {
                return;
            }
            events.add(Event.quadStart(l, b, t, LR, L, R, C));
            events.add(Event.quadEnd(r, b, t, LR, L, R, C));
        }

        static class Event implements Comparable<Event> {
            public final int x;
            public final int type;
            public final int l;
            public final int r;
            public final long LR;
            public final long L;
            public final long R;
            public final long C;
            public final int index;

            public Event(int x, int type, int l, int r, long LR, long l1, long r1, long c, int index) {
                this.x = x;
                this.type = type;
                this.l = l;
                this.r = r;
                this.LR = LR;
                L = l1;
                R = r1;
                C = c;
                this.index = index;
            }

            public static Event quadStart(int x, int l, int r, long LR, long L, long R, long C) {
                return new Event(x, -1, l, r, LR, L, R, C, -1);
            }

            public static Event quadEnd(int x, int l, int r, long LR, long L, long R, long C) {
                return new Event(x, 1, l, r, LR, L, R, C, -1);
            }

            public static Event point(int x, int index) {
                return new Event(x, 0, -1, -1, -1, -1, -1, -1, index);
            }


            public int compareTo(Event o) {
                int k = Integer.compare(x, o.x);
                return k != 0 ? k : Integer.compare(type, o.type);
            }

        }

    }

    static class Fenwick {
        public final int n;
        public final long[] a;

        public Fenwick(int n) {
            this.n = n;
            a = new long[n];
        }

        public long getValue(int r) {
            long result = 0;
            for (; r >= 0; r = (r & (r + 1)) - 1) {
                result += a[r];
            }
            return result;
        }

        public void update(int l, int r, long value) {
            if (l > r) {
                return;
            }
            update(r + 1, -value);
            update(l, value);
        }

        public void update(int x, long value) {
            for (; x < n; x = x | (x + 1)) {
                a[x] += value;
            }
        }
    }
}
