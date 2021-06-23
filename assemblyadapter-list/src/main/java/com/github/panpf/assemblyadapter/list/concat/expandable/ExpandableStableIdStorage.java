/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.assemblyadapter.list.concat.expandable;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

/**
 * Used by {@link ConcatExpandableListAdapter} to isolate item ids between nested adapters, if necessary.
 */
interface ExpandableStableIdStorage {
    @NonNull
    StableIdLookup createStableIdLookup();

    /**
     * Interface that provides {@link ExpandableNestedAdapterWrapper}s a way to map their local stable ids
     * into global stable ids, based on the configuration of the {@link ConcatExpandableListAdapter}.
     */
    interface StableIdLookup {
        long localToGlobal(long localId);
    }

    /**
     * Returns {@link ConcatExpandableListAdapter#NO_ID} for all positions. In other words, stable ids are not
     * supported.
     */
    class NoStableIdStorage implements ExpandableStableIdStorage {
        private final ExpandableStableIdStorage.StableIdLookup mNoIdLookup = new ExpandableStableIdStorage.StableIdLookup() {
            @Override
            public long localToGlobal(long localId) {
                return ConcatExpandableListAdapter.NO_ID;
            }
        };

        @NonNull
        @Override
        public ExpandableStableIdStorage.StableIdLookup createStableIdLookup() {
            return mNoIdLookup;
        }
    }

    /**
     * A pass-through implementation that reports the stable id in sub adapters as is.
     */
    class SharedPoolStableIdStorage implements ExpandableStableIdStorage {
        private final ExpandableStableIdStorage.StableIdLookup mSameIdLookup = new ExpandableStableIdStorage.StableIdLookup() {
            @Override
            public long localToGlobal(long localId) {
                return localId;
            }
        };

        @NonNull
        @Override
        public ExpandableStableIdStorage.StableIdLookup createStableIdLookup() {
            return mSameIdLookup;
        }
    }

    /**
     * An isolating implementation that ensures the stable ids among adapters do not conflict with
     * each-other. It keeps a mapping for each adapter from its local stable ids to a global domain
     * and always replaces the local id w/ a globally available ID to be consistent.
     */
    class IsolatedStableIdStorage implements ExpandableStableIdStorage {
        long mNextStableId = 0;

        long obtainId() {
            return mNextStableId++;
        }

        @NonNull
        @Override
        public ExpandableStableIdStorage.StableIdLookup createStableIdLookup() {
            return new WrapperStableIdLookup();
        }

        class WrapperStableIdLookup implements ExpandableStableIdStorage.StableIdLookup {
            private final LongSparseArray<Long> mLocalToGlobalLookup = new LongSparseArray<>();

            @Override
            public long localToGlobal(long localId) {
                Long globalId = mLocalToGlobalLookup.get(localId);
                if (globalId == null) {
                    globalId = obtainId();
                    mLocalToGlobalLookup.put(localId, globalId);
                }
                return globalId;
            }
        }
    }
}