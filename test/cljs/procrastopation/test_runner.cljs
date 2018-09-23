(ns procrastopation.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [procrastopation.core-test]
   [procrastopation.common-test]))

(enable-console-print!)

(doo-tests 'procrastopation.core-test
           'procrastopation.common-test)
