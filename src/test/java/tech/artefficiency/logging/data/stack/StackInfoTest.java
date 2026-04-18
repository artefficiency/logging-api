package tech.artefficiency.logging.data.stack;

import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.stubs.TestStackFrame;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.data.stack.StackInfoTest.Data.*;

public class StackInfoTest {

    interface Data {
        StackWalker.StackFrame[] FRAMES           = new StackWalker.StackFrame[]{
                new TestStackFrame("com.example.Service", "processOrder", 42),
                new TestStackFrame("com.example.Controller", "handle", 15)
        };
        StackWalker.StackFrame[] MORE_FRAMES      = new StackWalker.StackFrame[]{
                new TestStackFrame("com.example.Service", "processOrder", 42),
                new TestStackFrame("com.example.Controller", "handle", 15),
                new TestStackFrame("com.example.Api", "endpoint", 8)
        };
        StackWalker.StackFrame[] EMPTY_FRAMES     = new StackWalker.StackFrame[0];
        int                      FRAMES_SIZE      = FRAMES.length;
        int                      MORE_FRAMES_SIZE = MORE_FRAMES.length;
        int                      EMPTY_SIZE       = 0;
    }

    StackInfo target;
    StackInfo other;
    boolean   result;

    @Test
    public void ctor_default_createsCorrectInstance() {
        when:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
        }
        then:
        {
            assertThat(target.mode()).isEqualTo(StackMode.FAIR);
            assertThat(target.elements()).isEqualTo(FRAMES);
            assertThat(target.size()).isEqualTo(FRAMES_SIZE);
        }
    }

    @Test
    public void ctor_emptyArray_createsCorrectInstance() {
        when:
        {
            target = new StackInfo(StackMode.NONE, EMPTY_FRAMES, EMPTY_SIZE);
        }
        then:
        {
            assertThat(target.mode()).isEqualTo(StackMode.NONE);
            assertThat(target.elements()).isEqualTo(EMPTY_FRAMES);
            assertThat(target.size()).isEqualTo(EMPTY_SIZE);
        }
    }

    @Test
    public void accessors_returnCorrectValues() {
        when:
        {
            target = new StackInfo(StackMode.FULL, FRAMES, FRAMES_SIZE);
        }
        then:
        {
            assertThat(target.mode()).isEqualTo(StackMode.FULL);
            assertThat(target.elements()).isEqualTo(FRAMES);
            assertThat(target.size()).isEqualTo(FRAMES_SIZE);
        }
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    public void equals_differentMode_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FULL, FRAMES, FRAMES_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void equals_differentElements_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FAIR, MORE_FRAMES, MORE_FRAMES_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void equals_differentSize_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FAIR, FRAMES, MORE_FRAMES_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void equals_null_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = null;
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
        }
        when:
        {
            result = target.equals("not a StackInfo");
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void equals_sameEmptyArray_returnsTrue() {
        given:
        {
            target = new StackInfo(StackMode.NONE, EMPTY_FRAMES, EMPTY_SIZE);
            other  = new StackInfo(StackMode.NONE, EMPTY_FRAMES, EMPTY_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    public void equals_emptyVsNonEmpty_returnsFalse() {
        given:
        {
            target = new StackInfo(StackMode.NONE, EMPTY_FRAMES, EMPTY_SIZE);
            other  = new StackInfo(StackMode.NONE, FRAMES, FRAMES_SIZE);
        }
        when:
        {
            result = target.equals(other);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
        }
        when:
        {
            result = target.hashCode() == other.hashCode();
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    public void hashCode_differentValues_differentHashCode() {
        given:
        {
            target = new StackInfo(StackMode.FAIR, FRAMES, FRAMES_SIZE);
            other  = new StackInfo(StackMode.FULL, MORE_FRAMES, MORE_FRAMES_SIZE);
        }
        when:
        {
            result = target.hashCode() != other.hashCode();
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    public void hashCode_emptyArray_correctHashCode() {
        given:
        {
            target = new StackInfo(StackMode.NONE, EMPTY_FRAMES, EMPTY_SIZE);
        }
        when:
        {
            result = target.hashCode() == 0;
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }
}
