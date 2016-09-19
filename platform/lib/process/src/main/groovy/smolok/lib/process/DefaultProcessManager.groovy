/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smolok.lib.process

import static org.apache.commons.io.IOUtils.readLines

/**
 * Default implementation of process manager based on JDK process API.
 */
class DefaultProcessManager extends ExecutorBasedProcessManager {

    @Override
    List<String> execute(Command command) {
        if(log.isDebugEnabled()) {
            log.debug('About to execute command:', command.command())
        }

        try {
            def commandSegments = command.command()

            def sudoPassword = command.sudoPassword()
            if(command.sudo()) {
                if(sudoPassword == null) {
                    throw new IllegalStateException('Sudo access is required to execute the command. Please set up SUDO_PASSWORD environment variable or JVM system property.')
                } else if(System.getProperty("user.name") == 'root' || sudoPassword.isEmpty()) {
                    commandSegments.add(0, 'sudo')
                } else {
                    commandSegments = ['/bin/bash', '-c', "echo '${sudoPassword}'| sudo -S ${commandSegments.join(' ')}".toString()]
                }
            }

            def processBuilder = new ProcessBuilder().redirectErrorStream(true).command(commandSegments)
            if(command.workingDirectory() != null) {
                log.debug('Changing working directory to: {}', command.workingDirectory().absolutePath)
                processBuilder.directory(command.workingDirectory())
            }
            def process = processBuilder.start()
            def output = readLines(process.getInputStream())
            if(log.isDebugEnabled()) {
                log.debug('Output of the command {}: {}', commandSegments, output)
            }
            output
        } catch (IOException e) {
            throw new ProcessExecutionException(e)
        }
    }

}