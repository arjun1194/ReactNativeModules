import {NativeModules} from 'react-native';
const {TopHeadlines} = NativeModules;

interface ITopHeadlines {
  get(url: string, apiKey: string): Promise<object>;
}

const Api = {
  topHeadlines: (TopHeadlines as ITopHeadlines),
};

export default Api;
