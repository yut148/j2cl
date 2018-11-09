/*
 * Copyright 2017 Google Inc.
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
package com.google.j2cl.tools.gwtincompatible;

import com.google.j2cl.bazel.BazelWorker;
import com.google.j2cl.common.Problems;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

/** Runs The @GwtIncompatible stripper as a worker. */
public class BazelGwtIncompatibleStripper {
  @Argument(metaVar = "<source files .java|.srcjar>", usage = "source files")
  protected List<String> files = new ArrayList<>();

  @Option(
      name = "-d",
      required = true,
      metaVar = "<file>",
      usage = "The location into which to place output srcjar.")
  protected String outputPath;

  protected Problems run(String[] args) {
    Problems problems = new Problems();
    CmdLineParser parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      String message = e.getMessage() + "\n";
      message += "Valid options: \n" + parser.printExample(OptionHandlerFilter.ALL);
      message += "\nuse -help for a list of possible options in more details";
      problems.error(message);
      return problems;
    }
    return GwtIncompatibleStripper.strip(files, outputPath);
  }

  public static void main(String[] workerArgs) {
    BazelWorker.start(
        workerArgs,
        (args, output) ->
            new BazelGwtIncompatibleStripper().run(args).reportAndGetExitCode(output));
  }
}
