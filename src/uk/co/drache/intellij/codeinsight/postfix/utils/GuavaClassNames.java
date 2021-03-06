/*
 * Copyright (C) 2014 Bob Browning
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
package uk.co.drache.intellij.codeinsight.postfix.utils;

/**
 * Collection of static strings representing guava class names.
 *
 * @author Bob Browning
 */
public final class GuavaClassNames {

  /**
   * Base package.
   */
  private static final String COM_GOOGLE_COMMON_BASE = "com.google.common.base";

  /**
   * Collect package.
   */
  private static final String COM_GOOGLE_COMMON_COLLECT = "com.google.common.collect";

  /**
   * {@link com.google.common.collect.FluentIterable}.
   */
  public static final String FLUENT_ITERABLE = COM_GOOGLE_COMMON_COLLECT + ".FluentIterable";

  /**
   * {@link com.google.common.collect.ImmutableList}.
   */
  public static final String IMMUTABLE_LIST = COM_GOOGLE_COMMON_COLLECT + ".ImmutableList";

  /**
   * {@link com.google.common.collect.ImmutableSet}.
   */
  public static final String IMMUTABLE_SET = COM_GOOGLE_COMMON_COLLECT + ".ImmutableSet";

  /**
   * {@link com.google.common.collect.Iterables}.
   */
  public static final String ITERABLES = COM_GOOGLE_COMMON_COLLECT + ".Iterables";

  /**
   * {@link com.google.common.base.Joiner}.
   */
  public static final String JOINER = COM_GOOGLE_COMMON_BASE + ".Joiner";

  /**
   * {@link com.google.common.base.Splitter}.
   */
  public static final String SPLITTER = COM_GOOGLE_COMMON_BASE + ".Splitter";

  /**
   * {@link com.google.common.base.Preconditions}.
   */
  public static final String PRECONDITIONS = COM_GOOGLE_COMMON_BASE + ".Preconditions";
}
