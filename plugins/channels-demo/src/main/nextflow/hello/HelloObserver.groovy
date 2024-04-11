/*
 * Copyright 2021, Seqera Labs
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

package nextflow.hello

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.trace.TraceObserver

import nextflow.processor.TaskHandler
import nextflow.trace.TraceRecord
import java.nio.file.Path
import groovy.json.JsonOutput

/**
 * Example workflow events observer
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Slf4j
@CompileStatic
class HelloObserver implements TraceObserver {

    List<TaskHandler> tasks = []
    Map<Path,Path> files = [:]

    @Override
    void onFlowCreate(Session session) {
        log.info "Hello Channels podcast! 🎙️"
    }

    @Override
    void onFlowComplete() {
        log.info "Pipeline complete! 🎉"
        final myJson = [
            tasks: tasks.collect { it -> [name: it.task.name] },
            files: files.collect { source,dest -> [source: source.toUriString(), dest: dest.toUriString()] },
            author: "Phil"
        ]
        log.info "JSON: $myJson"
        final path = "channels-demo.json" as Path
        path.text = JsonOutput.prettyPrint(JsonOutput.toJson(myJson))
        log.info "JSON file written: ${path.toUriString()}"
    }

    @Override
    void onProcessComplete(TaskHandler handler, TraceRecord trace){
        log.info "onProcessComplete was invoked! ${handler.task.name}"
        tasks << handler
    }

    @Override
    void onProcessCached(TaskHandler handler, TraceRecord trace){
        log.info "onProcessCached was invoked! ${handler.task.name}"
        tasks << handler
    }

    @Override
    void onFilePublish(Path destination, Path source){
        log.info "onFilePublish was invoked! ${source.toUriString()} -> ${destination.toUriString()}"
        files[source] = destination
    }

}
