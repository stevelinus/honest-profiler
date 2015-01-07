/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.sources.LogSource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Application Service for monitoring logs
 */
public class Monitor {

    public void pipeFile(final LogSource logSource, final ProfileListener listener) {
        ProfileUpdateModerator moderator = new ProfileUpdateModerator(getLogger(ProfileUpdateModerator.class), listener);
        moderator.start();

        final Conductor conductor = pipe(logSource, moderator, true);
        new ThreadedAgent(getLogger(ThreadedAgent.class), conductor::run).start();
    }

    public void consumeFile(final LogSource logSource, final ProfileListener listener) {
        Conductor consumer = pipe(logSource, listener, false);
        while (consumer.run())
            ;
    }

    private Conductor pipe(final LogSource logSource, final ProfileListener listener, final boolean continuous) {
        LogCollector collector = new LogCollector(listener, continuous);
        LogParser parser = new LogParser(getLogger(LogParser.class), collector);
        return new Conductor(getLogger(Conductor.class), logSource, parser, continuous);
    }

}
