package OutputFile;

public class Procedure {
    public static class PartialProcedure {
        private final int start;
        private final String name;

        public PartialProcedure(String name, int begin) {
            this.start = begin;
            this.name = name;
        }

        public Procedure build(int end) {
            return new Procedure(name, start, end);
        }
        public String getName(){
            return name;
        }
    }

    private final int start;
    private final int end;
    private final String name;

    public Procedure(String name, int begin, int finish) {
        start = begin;
        end = finish;
        this.name = name;
    }

    int getStart() {
        return start;
    }

    String getName() {
        return name;
    }

    int getEnd() {
        return end;
    }

}
