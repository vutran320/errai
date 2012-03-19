/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen.framework.builder.callstack;

import org.jboss.errai.codegen.framework.Context;
import org.jboss.errai.codegen.framework.Statement;
import org.jboss.errai.codegen.framework.exception.GenerationException;
import org.jboss.errai.codegen.framework.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DynamicLoad extends AbstractCallElement {
  private Object value;

  public DynamicLoad(Object value) {
    this.value = value;
  }

  @Override
  public void handleCall(CallWriter writer, Context context, Statement statement) {
    try {
      nextOrReturn(writer, context, GenUtil.generate(context, value));
    }
    catch (GenerationException e) {
      blameAndRethrow(e);
    }
  }

  @Override
  public String toString() {
    return "[[DynamicLoad<" + value + ">]" + next + "]";
  }

  public Object getValue() {
    return value;
  }
}
