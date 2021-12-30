package ir.smmh.lingu;

import java.util.Objects;

public abstract class IndividualTokenType implements Token.Type.Individual {
    public final String title;

    public IndividualTokenType(String title) {
        this.title = title;
    }

    @Override
    public final String toString() {
        return title;
    }

    public class IndividualToken implements Token.Individual {
        public final String data;
        public final int position;

        public IndividualToken(String data, int position) {
            this.data = Objects.requireNonNull(data);
            this.position = position;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof IndividualToken) {
                IndividualToken token = (IndividualToken) other;
                return IndividualTokenType.this.toString().equals(IndividualTokenType.this.toString()) && this.data.equals(token.data);
            } else {
                System.out.print("Cannot compare token with: ");
                System.out.println(other.toString());
                return false;
            }
        }

        @Override
        public String toString() {
            return "<" + getData() + ">";
        }

        public IndividualTokenType getType() {
            return IndividualTokenType.this;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public String getData() {
            return data;
        }

        @Override
        public Token.Individual getFirstHandle() {
            return this;
        }

        @Override
        public Token.Individual getLastHandle() {
            return this;
        }

        public class BrokenPiece extends IndividualToken {
            public BrokenPiece(int start, int end) {
                super(IndividualToken.this.getData().substring(start, end), IndividualToken.this.getPosition() + start);
            }

            public BrokenPiece(int start) {
                super(IndividualToken.this.getData().substring(start), IndividualToken.this.getPosition() + start);
            }
        }
    }
}
