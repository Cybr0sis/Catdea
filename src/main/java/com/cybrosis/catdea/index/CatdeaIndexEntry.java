/*
 * Copyright 2019 Cybrosis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cybrosis.catdea.index;

import com.android.ddmlib.Log;
import com.cybrosis.catdea.CatdeaInfo;
import com.intellij.openapi.util.io.DataInputOutputUtilRt;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.io.DataExternalizer;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

/**
 * @author cybrosis
 */
public class CatdeaIndexEntry implements CatdeaInfo {
    public static final DataExternalizer<Collection<CatdeaIndexEntry>> EXTERNALIZER = new DataExternalizer<Collection<CatdeaIndexEntry>>() {
        @Override
        public void save(@NotNull DataOutput out, Collection<CatdeaIndexEntry> values) throws IOException {
            DataInputOutputUtilRt.writeSeq(out, values, value -> value.serialize(out));
        }

        @Override
        public Collection<CatdeaIndexEntry> read(@NotNull DataInput in) throws IOException {
            return DataInputOutputUtilRt.readSeq(in, () -> new CatdeaIndexEntry(in));
        }
    };

    public final int callOffset;
    public final Log.LogLevel level;
    public final @Language("RegExp") String msgPattern;
    public final @Language("RegExp") String tagPattern;


    @Contract(pure = true)
    CatdeaIndexEntry(@NotNull PsiMethodCallExpression call,
                     @NotNull Log.LogLevel level,
                     @NotNull @Language("RegExp") String msgPattern,
                     @NotNull @Language("RegExp") String tagPattern) {
        this.callOffset = call.getTextOffset();
        this.level = level;
        this.msgPattern = msgPattern;
        this.tagPattern = tagPattern;
    }

    private CatdeaIndexEntry(@NotNull DataInput in) throws IOException {
        this.callOffset = in.readInt();
        this.level = Log.LogLevel.getByLetter(in.readChar());
        assert level != null;
        this.msgPattern = in.readUTF();
        this.tagPattern = in.readUTF();
    }

    private void serialize(@NotNull DataOutput out) throws IOException {
        out.writeInt(callOffset);
        out.writeChar(level.getPriorityLetter());
        out.writeUTF(msgPattern);
        out.writeUTF(tagPattern);
    }

    @NotNull
    @Override
    public Log.LogLevel getLevel() {
        return level;
    }

    @NotNull
    @Override
    public String getTagPattern() {
        return tagPattern;
    }

    @NotNull
    @Override
    public String getMsgPattern() {
        return msgPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return callOffset == ((CatdeaIndexEntry) o).callOffset;
    }

    @Override
    public int hashCode() {
        return callOffset;
    }

    @Override
    public String toString() {
        return "Entry{" + callOffset + ", " + level +
               ", msg=\'" + msgPattern + '\'' +
               ", tag=\'" + tagPattern + '\'' +
               '}';
    }
}
