import {NativeModules} from 'react-native';
const {CallbackPromise} = NativeModules;

interface ICallbackPromise {
  doWork(name: String, callback: (any?: any) => void): Promise<any>;
}

export default CallbackPromise as ICallbackPromise;
