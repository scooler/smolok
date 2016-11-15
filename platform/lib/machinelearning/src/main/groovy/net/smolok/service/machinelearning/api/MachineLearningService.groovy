package net.smolok.service.machinelearning.api

interface MachineLearningService {

    void storeTrainingData(String collection, FeatureVector featureVector)

    List<Double> predict(String collection, FeatureVector featureVector)

}