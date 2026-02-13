package com.techLabs.nbpdcl.retrofit;

import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.models.ConsumerModel;
import com.techLabs.nbpdcl.models.DashBoardModel;
import com.techLabs.nbpdcl.models.DeleteFeature;
import com.techLabs.nbpdcl.models.DeviceIdModel;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.ExistNetworkModel;
import com.techLabs.nbpdcl.models.FeederModel;
import com.techLabs.nbpdcl.models.FindDeviceModel;
import com.techLabs.nbpdcl.models.Line.Cable;
import com.techLabs.nbpdcl.models.Line.Overhead;
import com.techLabs.nbpdcl.models.Line.Unbalanced;
import com.techLabs.nbpdcl.models.LoginModel;
import com.techLabs.nbpdcl.models.Logout;
import com.techLabs.nbpdcl.models.MainFeeder;
import com.techLabs.nbpdcl.models.ProjectModel;
import com.techLabs.nbpdcl.models.ReportNameModel;
import com.techLabs.nbpdcl.models.SelectedFeedersModel;
import com.techLabs.nbpdcl.models.Topology;
import com.techLabs.nbpdcl.models.UpdateModel;
import com.techLabs.nbpdcl.models.VerifyOTP;
import com.techLabs.nbpdcl.models.VoltageModel;
import com.techLabs.nbpdcl.models.analysis.AnalysisInformationModel;
import com.techLabs.nbpdcl.models.analysis.LoadAllocationModel;
import com.techLabs.nbpdcl.models.analysis.LoadFlowModel;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitAnalysisModel;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitBoxModel;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitDetailedModel;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitModel;
import com.techLabs.nbpdcl.models.device.Breaker;
import com.techLabs.nbpdcl.models.device.Fuse;
import com.techLabs.nbpdcl.models.device.ShuntCapacitor;
import com.techLabs.nbpdcl.models.device.Source;
import com.techLabs.nbpdcl.models.device.SpotLoad;
import com.techLabs.nbpdcl.models.device.Switch;
import com.techLabs.nbpdcl.models.device.Transformer;
import com.techLabs.nbpdcl.models.loadflow.LoadFlowBoxData;
import com.techLabs.nbpdcl.models.nsc.NewConnectionModel;
import com.techLabs.nbpdcl.models.report.AbnormalReport;
import com.techLabs.nbpdcl.models.report.DetailedReport;
import com.techLabs.nbpdcl.models.report.OverLoadConductorReport;
import com.techLabs.nbpdcl.models.report.OverLoadLineAndCablesReport;
import com.techLabs.nbpdcl.models.report.OverLoadTransformerReport;
import com.techLabs.nbpdcl.models.report.TransformerReport;
import com.techLabs.nbpdcl.models.survey.ConsumerResponse;
import com.techLabs.nbpdcl.models.trace.Tracing;
import com.techLabs.nbpdcl.models.zoom.ZoomToLayer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("AdminPanel/user/login/")
    Call<LoginModel> getLogin(@Body JsonObject jsonObject);
    @GET("Project/")
    Call<ProjectModel> getProject(@Header("Authorization") String accessToken, @Query("user_id") String userId);

    @POST("dashboard/")
    Call<DashBoardModel> getDashboardData(@Body JsonObject jsonObject);

    @POST("dashboard/")
    Call<MainFeeder> getMainFeederData(@Body JsonObject jsonObject);

    @POST("dashboard/")
    Call<FeederModel> getFeederData(@Body JsonObject jsonObject);

    @POST
    Call<JsonObject> getNetworkData(@Url String url, @Body JsonObject requestData);

    @POST("LayermodelInfo/")
    Call<Cable> getCableData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Overhead> getOverheadData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Unbalanced> getUnbalancedData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Breaker> getBreakerData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Fuse> getFuseData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<SpotLoad> getSpotLoadData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Switch> getSwitchData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Transformer> getTransformerData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<ShuntCapacitor> getShuntCapacitorData(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<Source> getSourceData(@Body JsonObject jsonObject);

    @POST("networkzoomtolayer/")
    Call<ZoomToLayer> netWorkZoomToLayer(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<EquipmentModel> getEquipmentData(@Body JsonObject jsonObject);

    @POST("trace/")
    Call<Tracing> getTracingData(@Body JsonObject jsonObject);

    @POST("topology/")
    Call<Topology> getTopologyData(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<ShortCircuitAnalysisModel> getLocationData(@Body JsonObject jsonObject);

    @POST("loadflowbox/")
    Call<LoadFlowBoxData> getLoadFlowBoxData(@Body JsonObject jsonObject);

    @POST("report/")
    Call<TransformerReport> getTransformerReport(@Body JsonObject jsonObject);

    @POST("reportName/")
    Call<ReportNameModel> getReportName(@Body JsonObject jsonObject);

    @POST("report/")
    Call<AbnormalReport> getAbnormalReport(@Body JsonObject jsonObject);

    @POST("report/")
    Call<DetailedReport> getDetailedReport(@Body JsonObject jsonObject);

    @POST("report/")
    Call<OverLoadConductorReport> getOverLoadConductorReport(@Body JsonObject jsonObject);

    @POST("report/")
    Call<OverLoadLineAndCablesReport> getOverLoadLineAndCablesReport(@Body JsonObject jsonObject);

    @POST("report/")
    Call<OverLoadTransformerReport> getOverLoadTransformerReport(@Body JsonObject jsonObject);

    @POST("shortcircuit/")
    Call<ShortCircuitModel> ShortCircuit(@Body JsonObject jsonObject);

    @POST("loadflow/")
    Call<LoadFlowModel> LoadFlow(@Body JsonObject jsonObject);

    @POST("shortcircuitbox/")
    Call<ShortCircuitBoxModel> ShortCircuitBox(@Body JsonObject jsonObject);

    @POST("report/")
    Call<ShortCircuitDetailedModel> ShortCircuitDetailed(@Body JsonObject jsonObject);

    @POST("newconnectionlt/")
    Call<NewConnectionModel> getNewConnectionData(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<VoltageModel> getVoltage(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<DeviceIdModel> getDeviceId(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<EquipmentModel> getEquipment(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<EquipmentModel> getEquipmentSurveyData(@Body JsonObject jsonObject);

    @POST("deletefeature/")
    Call<DeleteFeature> DeleteFeatures(@Body JsonObject jsonObject);

    @POST("LayermodelInfo/")
    Call<FindDeviceModel> FindDevice(@Body JsonObject jsonObject);

    @POST("report/")
    Call<ResponseBody> downloadExcel(@Body JsonObject jsonObject);

    @POST("networklist/")
    Call<ExistNetworkModel> getExistNetworkData(@Body JsonObject jsonObject);

    @POST("SBPDCL_Survey/SurveyDataUpdate/")
    Call<UpdateModel> updateData(@Body JsonObject jsonObject);

    @POST("loadallocation")
    Call<LoadAllocationModel> LoadAllocation(@Body JsonObject jsonObject);

    @POST("AnalysisInformation")
    Call<AnalysisInformationModel> AnalysisInformatio(@Body JsonObject jsonObject);

    @POST("Selected_Feeder/")
    Call<SelectedFeedersModel> getSelectedFeeder(@Body JsonObject jsonObject);

    @POST("AdminPanel/user/verify_otp/")
    Call<VerifyOTP> otpVerify(@Body JsonObject jsonObject);

    @POST("AdminPanel/user/logout/")
    Call<Logout> logout(@Body JsonObject jsonObject);

    @POST("EquipmentInfo/")
    Call<ConsumerModel> consumerData(@Body JsonObject jsonObject);

    @GET("AdminPanel/user/Survey_Groups/")
    Call<ConsumerResponse> getNetworkName(@Header("Authorization") String accessToken);

}