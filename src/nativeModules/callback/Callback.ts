import {NativeModules} from 'react-native';
const {CallbackModule} = NativeModules;

interface Sleeper {
  sleep(seconds: number, callback: (args: any) => void): void;
}

export default CallbackModule as Sleeper;
