/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.generator;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.j2cl.ast.CompilationUnit;
import com.google.j2cl.ast.Method;
import com.google.j2cl.ast.TypeReference;
import com.google.j2cl.ast.Variable;
import com.google.j2cl.ast.visitors.ImportGatheringVisitor;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * Utility functions to transpile the j2cl AST.
 */
public class TranspilerUtils {
  public static String getSourceName(TypeReference typeReference) {
    // TODO(rluble): Stub implementation. Needs to be implemented for the cases in which a
    // class might be refered by multiple different type references.
    // TODO(rluble): See if the canonical name concept can be avoided in our AST but converting
    // to canonical type references at AST construction.
    return typeReference.getSourceName();
  }

  /**
   * Returns the unqualified name that will be used in JavaScript.
   */
  public static String getClassName(TypeReference typeReference) {
    //TODO(rluble): Stub implementation.
    return typeReference.getSimpleName();
  }

  /**
   * Returns the JsDoc type name.
   */
  public static String getJsDocName(TypeReference typeReference) {
    // TODO: Incomplete implementation.
    if (typeReference.isArray()) {
      return String.format(
          "%s%s%s",
          Strings.repeat("Array<", typeReference.getDimensions()),
          getJsDocName(typeReference.getLeafType()),
          Strings.repeat(">", typeReference.getDimensions()));
    }
    switch (typeReference.getSourceName()) {
      case "int":
      case "double":
      case "float":
      case "short":
        return "number";
      case "java.lang.String":
        return "string";
    }
    return getClassName(typeReference);
  }

  /**
   * Returns the relative output path for a given compilation unit.
   */
  public static String getOutputPath(CompilationUnit compilationUnit) {
    String unitName = compilationUnit.getName();
    String packageName = compilationUnit.getPackageName();
    return packageName.replace('.', '/') + "/" + unitName;
  }

  private static final Import IMPORT_CLASS = new Import("Class", "gen.java.lang.ClassModule");
  private static final Import IMPORT_BOOTSTRAP_UTIL =
      new Import("Util", "nativebootstrap.UtilModule");

  /**
   * Returns the relative output path for a given compilation unit.
   */
  public static Set<Import> getImports(CompilationUnit compilationUnit) {

    Set<Import> imports = new TreeSet<>();
    imports.add(IMPORT_CLASS);
    imports.add(IMPORT_BOOTSTRAP_UTIL);
    imports.addAll(
        FluentIterable.from(ImportGatheringVisitor.gatherImports(compilationUnit))
            .transform(
                new Function<TypeReference, Import>() {
                  @Nullable
                  @Override
                  public Import apply(TypeReference typeReference) {
                    return new Import(typeReference);
                  }
                })
            .toList());
    return imports;
  }

  public static String getParameterList(Method method) {
    List<String> parameterNameList =
        Lists.transform(
            method.getParameters(),
            new Function<Variable, String>() {
              @Override
              public String apply(Variable variable) {
                return variable.getName();
              }
            });
    return Joiner.on(",").join(parameterNameList);
  }

  private TranspilerUtils() {}
}
